import {Component, OnDestroy} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {NgForOf} from "@angular/common";
import {Subscription} from "rxjs";
import {PoolConfigService} from "../../../services/pool-config.service";
import {UserService} from "../../../services/user.service";
import {BackendMessageService} from "../../../services/backend-message.service";
import {TimingAction} from "../../../domain/event-bus-message";
import {User} from "../../../domain/user";

@Component({
    selector: 'app-control',
    standalone: true,
    imports: [
        MatCardModule,
        NgForOf
    ],
    templateUrl: './referees.component.html',
    styleUrl: './referees.component.css'
})
export class RefereesComponent implements OnDestroy {
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public lanes: Array<LaneWithReferees> = [];
    private subscription: Subscription;

    constructor(
        poolConfigService: PoolConfigService,
        private userService: UserService,
        messageService: BackendMessageService) {
        poolConfigService.get().subscribe(config => {
                config.lanes.forEach((lane) => this.lanes.push({lane}));
                this.refreshReferees();
            }
        );
        this.subscription = messageService.subscribe((message) => {
            if (message.action === TimingAction.USER_MOVE ||
                message.action === TimingAction.LOGIN) {
                this.refreshReferees();
            }
        })
    }

    private refreshReferees() {
        this.lanes.forEach((lane) => {
            this.userService.getByLane(lane.lane)
                .subscribe(referees =>
                    lane.referees = referees
                );
        })
    }
}

interface LaneWithReferees {
    lane: number;
    referees?: Array<User>;
}
