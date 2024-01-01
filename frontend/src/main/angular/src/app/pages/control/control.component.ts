import {Component, OnDestroy} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {PoolConfigService} from "../../services/pool-config.service";
import {NgForOf} from "@angular/common";
import {User} from "../../domain/user";
import {RegistrationService} from "../../services/registration.service";
import {BackendMessageService} from "../../services/backend-message.service";
import {TimingAction} from "../../domain/event-bus-message";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-control',
    standalone: true,
    imports: [
        MatCardModule,
        NgForOf
    ],
    templateUrl: './control.component.html',
    styleUrl: './control.component.css'
})
export class ControlComponent implements OnDestroy {
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    public lanes: Array<LaneWithReferees> = [];
    private subscription: Subscription;

    constructor(
        poolConfigService: PoolConfigService,
        private registrationService: RegistrationService,
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
            this.registrationService.getByLane(lane.lane)
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
