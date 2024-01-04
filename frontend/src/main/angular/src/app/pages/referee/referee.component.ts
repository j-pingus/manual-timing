import {Component, OnDestroy, OnInit} from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";
import {Subscription} from "rxjs";
import {EventService} from "../../services/events.service";
import {JsonPipe, NgForOf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {ActivatedRoute, Router} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatIconModule} from "@angular/material/icon";
import {SwimmingEvent} from "../../domain/swimming-event";
import {UserUtils} from "../../utils/user.utils";
import {User} from "../../domain/user";
import {InscriptionsService} from "../../services/inscriptions.service";
import {TimingAction} from "../../domain/event-bus-message";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {ManualTimeService} from "../../services/manual.time.service";
import {Heat} from "../../domain/heat";
import {ManualTimePipe} from "../../pipes/manual-time.pipe";
import {ManualTimeDirective} from "../../directives/manual-time.directive";

@Component({
  selector: 'app-referee',
  standalone: true,
  imports: [
    NgForOf,
    MatCardModule,
    MatButtonModule,
    MatTooltipModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    JsonPipe,
    ManualTimePipe,
    ManualTimeDirective
  ],
  templateUrl: './referee.component.html',
  styleUrl: './referee.component.css'
})
export class RefereeComponent implements OnDestroy, OnInit {
  private subscription: Subscription;
  public events: Array<SwimmingEvent> = [];
  public event: SwimmingEvent | undefined;
  public eventId = 1;
  public heats: Array<Heat> = [];
  public user: User;

  constructor(messageService: BackendMessageService,
              private eventService: EventService,
              private inscriptionsService: InscriptionsService,
              private manualTimeService: ManualTimeService,
              route: ActivatedRoute,
              private router: Router) {
    this.subscription = new Subscription();
    this.subscription.add(route.params.subscribe(params => {
      this.eventId = +params['id']; // (+) converts string 'id' to a number
      this.getEvents();

    }));
    this.subscription.add(messageService.subscribe(message => {
      //Refresh if data changed
      if (this.user.lane == message.laneId && this.eventId == message.eventId) {
        if (message.action === TimingAction.REFRESH_INSCRIPTIONS) {
          this.getEventInscriptions();
        }
        if (message.action === TimingAction.REFRESH_TIMES) {
          const heat = this.heats.find(h => h.id == message.heatId);
          if (heat) {
            heat.time = message.body;
          }
        }
      }
    }));
    this.user = UserUtils.getSavedUser();
  }

  ngOnInit(): void {
    if (!UserUtils.isRegistered()) {
      this.router.navigate(['/']);
    }
    this.user = UserUtils.getSavedUser();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private getEvents() {
    this.subscription.add(this.eventService.getAll().subscribe(
      (data) => {
        this.events = data;
        if (this.eventId < 1) {
          this.eventId = 1;
        }
        if (this.eventId >= data.length) {
          this.eventId = data.length;
        }
        this.event = data[this.eventId - 1];
        this.heats = [];
        for (let i = 1; i <= data[this.eventId - 1].heats; i++) {
          this.heats.push({id: i});
        }
        this.getEventInscriptions();
        this.getEventTimes();
      }
    ));
  }

  previousEvent() {
    this.router.navigate(['/referee', this.eventId - 1]);
  }

  nextEvent() {
    this.router.navigate(['/referee', this.eventId + 1]);
  }

  private getEventInscriptions() {
    if (this.user.lane && this.event?.id) {
      this.subscription.add(
        this.inscriptionsService.getByEventAndLane(this.event?.id, this.user.lane).subscribe(inscriptions => {
          inscriptions.forEach(inscription => {
            this.heats[inscription.heat - 1].swimmer = inscription.name;
          })
        })
      );
    }
  }

  private getEventTimes() {
    if (this.user.lane && this.event?.id) {
      this.subscription.add(
        this.manualTimeService.getByEventAndLane(this.event?.id, this.user.lane).subscribe(inscriptions => {
          inscriptions.forEach(time => {
            this.heats[time.heat - 1].time = time.time;
          })
        })
      );
    }
  }

  save(heat: Heat) {
    if (this.user.lane && this.eventId && heat.time) {
      this.manualTimeService.save(
        {
          time: heat.time,
          heat: heat.id,
          lane: this.user.lane,
          event: this.eventId
        }).subscribe();
    }
  }

}


