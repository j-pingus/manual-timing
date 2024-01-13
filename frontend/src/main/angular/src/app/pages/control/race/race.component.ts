import {Component, HostListener, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {SwimmingEvent} from "../../../domain/swimming-event";
import {Subscription} from "rxjs";
import {PoolConfigService} from "../../../services/pool-config.service";
import {EventService} from "../../../services/events.service";
import {InscriptionsService} from "../../../services/inscriptions.service";
import {ManualTimeService} from "../../../services/manual.time.service";
import {BackendMessageService} from "../../../services/backend-message.service";
import {JsonPipe, NgForOf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {TimingAction} from "../../../domain/event-bus-message";
import {Lane} from "../../../domain/Lane";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {ManualTimePipe} from "../../../pipes/manual-time.pipe";
import {Inscription} from "../../../domain/inscription";
import {MatDialog} from "@angular/material/dialog";
import {InscriptionComponent} from "../../../dialogs/inscription/inscription.component";
import {TimeRecord} from "../../../domain/time-record";

@Component({
  selector: 'app-race',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    ManualTimePipe,
    JsonPipe
  ],
  templateUrl: './race.component.html',
  styleUrl: './race.component.css'
})
export class RaceComponent implements OnDestroy {
  public eventId: number = 1;
  public heatId: number = 1;
  public events: Array<SwimmingEvent> = [];
  public lanes: Array<Lane> = [];
  private subscription: Subscription = new Subscription();
  public maxEventId: number = 0;
  public maxHeatId: number = 0;
  private laneDelta: number = 0;
  public event: SwimmingEvent = {
    heats: -1,
    id: -1,
    description: 'not found',
    time: "99:99",
    date: "99/99/9999",
    distances: []
  };

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(
    route: ActivatedRoute,
    private router: Router,
    private poolConfigService: PoolConfigService,
    private eventService: EventService,
    private inscriptionService: InscriptionsService,
    private manualTimeService: ManualTimeService,
    private backendMessageService: BackendMessageService,
    private dialog: MatDialog) {
    this.subscription.add(route.params.subscribe(params => {
      this.eventId = +params['event']; // (+) converts string 'id' to a number
      this.heatId = +params['heat'];
      this.loadConfig();
    }));
    this.subscription.add(
      this.backendMessageService.subscribe(message => {
        if (message.eventId == this.eventId && message.heatId == this.heatId) {
          if (message.action === TimingAction.REFRESH_INSCRIPTIONS) {
            this.loadInscriptions();
          }
          if (message.action == TimingAction.REFRESH_TIMES) {
            this.setTime(this.lanes[message.laneId - this.laneDelta], {distance: message.distance, time: message.body});
          }
        }
      })
    );
  }

  private loadConfig() {
    this.subscription.add(
      this.poolConfigService.get().subscribe(config => {
        this.laneDelta = config.lanes[0];
        this.lanes = config.lanes.map(lane => {
          return {
            lane,
            inscription: {lane, name: "", heat: -1, entrytime: "", clubcode: "", event: -1, nation: "", agetext: ""},
            times: []
          };
        });
        this.loadEvents();
        this.loadInscriptions();
        this.loadTimes();
      })
    );
  }

  private loadEvents() {
    this.subscription.add(
      this.eventService.getAll().subscribe(events => {
        this.event = events.find(e => e.id == this.eventId) || this.event;
        this.maxEventId = events[events.length - 1].id;
        this.maxHeatId = this.event.heats;
      })
    )
  }

  public nextEvent() {
    if (this.eventId < this.maxEventId) {
      this.router.navigate(['/control/race', this.eventId + 1, 1]);
    }
  }

  public previousEvent() {
    if (this.eventId > 1) {
      this.router.navigate(['/control/race', this.eventId - 1, 1]);
    }
  }

  @HostListener('window:keydown.control.n', ['$event'])
  nextHeatShortcut(event: KeyboardEvent) {
    event.preventDefault();
    this.nextHeat();
  }

  @HostListener('window:keydown.control.p', ['$event'])
  previousHeatShortcut(event: KeyboardEvent) {
    event.preventDefault();
    this.previousHeat();
  }

  public nextHeat() {
    if (this.heatId < this.maxHeatId) {
      this.router.navigate(['/control/race', this.eventId, this.heatId + 1]);
    } else {
      this.nextEvent();
    }
  }

  public previousHeat() {
    if (this.heatId > 1) {
      this.router.navigate(['/control/race', this.eventId, this.heatId - 1]);
    } else {
      this.previousEvent();
    }
  }

  private loadInscriptions() {
    this.subscription.add(
      this.inscriptionService.getByEventAndHeat(this.eventId, this.heatId).subscribe(inscriptions => {
        inscriptions.forEach(inscription => {
          this.lanes[inscription.lane - this.laneDelta].inscription = inscription;
        })
      })
    );
  }

  private loadTimes() {
    this.subscription.add(
      this.manualTimeService.getByEventAndHeat(this.eventId, this.heatId).subscribe(times => {
        times.forEach(time => {
          this.setTime(this.lanes[time.lane - this.laneDelta], {distance: time.distance, time: time.time});
        });
      })
    );
  }

  private setTime(lane: Lane, timeRecord: TimeRecord) {
    //TODO: order
    lane.times.push(timeRecord);
  }

  popup(inscription: Inscription) {
    this.dialog.open(InscriptionComponent, {data: inscription});
  }
}


