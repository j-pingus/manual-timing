import {Component, OnDestroy} from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";
import {Subscription} from "rxjs";
import {EventService} from "../../services/events.service";
import {SwimmingEvent} from "../../domain/SwimmingEvent";
import {NgForOf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {ActivatedRoute, Router} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatIconModule} from "@angular/material/icon";

@Component({
    selector: 'app-referee',
    standalone: true,
    imports: [
        NgForOf,
        MatCardModule,
        MatButtonModule,
        MatTooltipModule,
        MatIconModule
    ],
    templateUrl: './referee.component.html',
    styleUrl: './referee.component.css'
})
export class RefereeComponent implements OnDestroy {
    private subscription: Subscription;
    public events: Array<SwimmingEvent> = [];
    public event: SwimmingEvent | undefined;
    public eventId = 0;
    public heats: Array<Heat> = [];

    constructor(messageService: BackendMessageService,
                private eventService: EventService,
                route: ActivatedRoute,
                private router: Router) {
        this.subscription = new Subscription();
        this.subscription.add(
            route.params.subscribe(params => {
                this.eventId = +params['id']; // (+) converts string 'id' to a number
                this.getEvents(this.eventId);

            }));
        this.subscription.add(messageService.subscribe(message =>
            //We can react from messages coming from server
            console.log('referee got message ', message)));
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    private getEvents(eventId: number) {
        this.subscription.add(this.eventService.getAll().subscribe(
            (data) => {
                this.events = data;
                if (this.eventId < 0) {
                    this.eventId = 0;
                }
                if (this.eventId >= data.length) {
                    this.eventId = data.length - 1;
                }
                this.event = data[this.eventId];
                this.heats = [];
                for (let i = 1; i <= this.event.heats; i++) {
                    //Fixme: putting an empty lane here and there
                    if (i != this.event.id) {
                        this.heats.push({id: i, name: ' swimmer xxxxx'});
                    } else {
                        this.heats.push({id: i, name: ''});

                    }
                }
            }
        ));
    }

    previousEvent() {
        this.router.navigate(['/referee', this.eventId - 1]);
    }

    nextEvent() {
        this.router.navigate(['/referee', this.eventId + 1]);
    }
}

interface Heat {
    id: number;
    name: string;
}
