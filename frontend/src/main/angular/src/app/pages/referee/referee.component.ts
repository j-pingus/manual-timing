import {Component, OnDestroy, OnInit} from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";
import {Subscription} from "rxjs";
import {EventService} from "../../services/events.service";
import {JsonPipe, NgForOf, NgIf} from "@angular/common";
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
import {FormsModule, NgControl} from "@angular/forms";
import {ManualTimeService} from "../../services/manual.time.service";
import {Heat} from "../../domain/heat";
import {ManualTimePipe} from "../../pipes/manual-time.pipe";
import {ManualTimeDirective} from "../../directives/manual-time.directive";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Inscription} from "../../domain/inscription";
import {InscriptionComponent} from "../../dialogs/inscription/inscription.component";
import {MatDialog} from "@angular/material/dialog";
import {TimeRecord} from "../../domain/time-record";
import {TimerComponent} from "../../dialogs/timer/timer.component";
import {TimerData} from "../../domain/timer-data";
import {PoolConfigService} from "../../services/pool-config.service";
import {PoolConfig} from "../../domain/pool-config";

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
    ManualTimeDirective,
    NgIf
  ],
  templateUrl: './referee.component.html',
  styleUrl: './referee.component.css'
})
export class RefereeComponent implements OnDestroy, OnInit {
  public events: Array<SwimmingEvent> = [];
  public event: SwimmingEvent | undefined;
  public eventId = 1;
  public heats: Array<Heat> = [];
  public user: User;
  private manualTime = new ManualTimePipe();
  private subscription: Subscription;
  private poolConfig: PoolConfig = {length: 0, lanes: [], bothEndsTiming: false, minTimeSeconds: 0};

  constructor(messageService: BackendMessageService,
              private eventService: EventService,
              private inscriptionsService: InscriptionsService,
              private manualTimeService: ManualTimeService,
              route: ActivatedRoute,
              private router: Router,
              private snackBar: MatSnackBar,
              private dialog: MatDialog,
              private poolConfigService: PoolConfigService) {
    this.subscription = new Subscription();
    this.subscription.add(route.params.subscribe(params => {
      this.eventId = +params['id']; // (+) converts string 'id' to a number
      this.getPoolConfig();

    }));
    this.subscription.add(messageService.subscribe(message => {
      //Refresh if data changed
      if (this.eventId == message.eventId) {
        if (message.action === TimingAction.REFRESH_INSCRIPTIONS) {
          this.getEventInscriptions();
        }
        if (this.user.lane == message.laneId && message.action === TimingAction.REFRESH_TIMES) {
          this.heats[message.heatId - 1].times.forEach(tr => {
            if (tr.distance == message.distance) {
              tr.time = this.manualTime.transform(message.body);
            }
          })
        }
      }
    }));
    this.user = UserUtils.getSavedUser();
  }

  nextEvent() {
    this.router.navigate(['/referee', this.eventId + 1]);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    if (!UserUtils.isRegistered()) {
      this.router.navigate(['/']);
    }
    this.user = UserUtils.getSavedUser();
  }

  previousEvent() {
    this.router.navigate(['/referee', this.eventId - 1]);
  }

  save(heatId: number, timeRecord: TimeRecord, model: NgControl) {
    if (this.user.lane != undefined && this.eventId && !model.pristine) {
      this.manualTimeService.save(
        {
          time: this.manualTime.transform(timeRecord.time),
          distance: timeRecord.distance,
          heat: heatId,
          lane: this.user.lane,
          event: this.eventId
        }).subscribe(() => {
          model.reset(timeRecord.time);
          this.snackBar.open('Time saved', undefined, {
            verticalPosition: 'top'
          })
        }
      );
    }
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
        var times: Array<TimeRecord> = [];
        this.event.distances.forEach(distance => times.push({distance, time: ''}));
        for (let i = 1; i <= data[this.eventId - 1].heats; i++) {
          this.heats.push({id: i, times: JSON.parse(JSON.stringify(times))});
        }
        this.getEventInscriptions();
        this.getEventTimes();
      }
    ));
  }

  private getEventInscriptions() {
    if (this.user.lane != undefined && this.event) {
      this.subscription.add(
        this.inscriptionsService.getByEventAndLane(this.event.id, this.user.lane).subscribe(inscriptions => {
          inscriptions.forEach(inscription => {
            this.heats[inscription.heat - 1].inscription = inscription;
          });
        })
      );
    }
  }

  private getEventTimes() {
    if (this.user.lane != undefined && this.event) {
      this.subscription.add(
        this.manualTimeService.getByEventAndLane(this.event.id, this.user.lane).subscribe(manualTime => {
          manualTime.forEach(time => {
            this.heats[time.heat - 1].times.forEach(
              timeRecord => {
                if (timeRecord.distance == time.distance) {
                  timeRecord.time = this.manualTime.transform(time.time);
                }
              }
            )
          })
        })
      );
    }
  }

  popup(inscription: Inscription | undefined) {
    if (inscription) {
      this.dialog.open(InscriptionComponent, {data: inscription});
    }
  }

  openTimer(heat: Heat) {
    if (this.user.lane) {
      var data: TimerData = {
        times: JSON.parse(JSON.stringify(heat.times)),
        eventId: this.eventId,
        heatId: heat.id,
        lane: this.user.lane,
        minimumDelay: this.poolConfig.minTimeSeconds * 1000
      }
      this.dialog.open(TimerComponent, {data});
    }
  }

  private getPoolConfig() {
    this.subscription.add(this.poolConfigService.get().subscribe((config) => {
      this.poolConfig = config;
      this.getEvents();
    }))
  }
}


