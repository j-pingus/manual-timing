import {Component, HostListener, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PoolConfigService} from "../../services/pool-config.service";
import {EventService} from "../../services/events.service";
import {InscriptionsService} from "../../services/inscriptions.service";
import {ManualTimeService} from "../../services/manual.time.service";
import {BackendMessageService} from "../../services/backend-message.service";
import {SplashService} from "../../services/splash.service";
import {MatDialog} from "@angular/material/dialog";
import {TimingAction} from "../../domain/event-bus-message";
import {SwimmingEvent} from "../../domain/swimming-event";
import {Lane} from "../../domain/Lane";
import {Subscription} from "rxjs";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {JsonPipe, NgForOf} from "@angular/common";
import {ManualTimePipe} from "../../pipes/manual-time.pipe";
import {Inscription} from "../../domain/inscription";
import {InscriptionComponent} from "../../dialogs/inscription/inscription.component";

@Component({
  selector: 'app-call-room',
  standalone: true,
  imports: [
    JsonPipe,
    ManualTimePipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTooltipModule,
    NgForOf
  ],
  templateUrl: './call-room.component.html',
  styleUrl: './call-room.component.css'
})
export class CallRoomComponent implements OnDestroy {
  private static PAGE = '/call-room';
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
    private splashService: SplashService,
    private dialog: MatDialog) {
    this.subscription.add(route.params.subscribe(params => {
      this.eventId = +params['event']; // (+) converts string 'id' to a number
      this.heatId = +params['heat'];
      this.loadConfig();
    }));
    this.subscription.add(
      this.backendMessageService.subscribe(message => {
        if (message.eventId == this.eventId && message.heatId == this.heatId) {
          if (message.action === TimingAction.REFRESH) {
            this.loadInscriptions();
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
      })
    );
  }

  private loadEvents() {
    this.subscription.add(
      this.eventService.getAll().subscribe(events => {
        this.event = events.find(e => e.id == this.eventId) || this.event;
        this.maxEventId = events[events.length - 1].id;
        this.maxHeatId = this.event.heats;
        for (let lane of this.lanes) {
          if (lane.times.length == 0) {
            lane.times = this.event.distances.map(distance => {
              return {time: '', distance}
            });
          }
        }
      })
    )
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

  public nextEvent() {
    if (this.eventId < this.maxEventId) {
      this.router.navigate([CallRoomComponent.PAGE, this.eventId + 1, 1]);
    }
  }

  public previousEvent() {
    if (this.eventId > 1) {
      this.router.navigate([CallRoomComponent.PAGE, this.eventId - 1, 1]);
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
      this.router.navigate([CallRoomComponent.PAGE, this.eventId, this.heatId + 1]);
    } else {
      this.nextEvent();
    }
  }

  public previousHeat() {
    if (this.heatId > 1) {
      this.router.navigate([CallRoomComponent.PAGE, this.eventId, this.heatId - 1]);
    } else {
      this.previousEvent();
    }
  }

  popup(inscription: Inscription) {
    this.dialog.open(InscriptionComponent, {data: inscription});
  }

}
