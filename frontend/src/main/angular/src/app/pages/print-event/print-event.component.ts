import {Component, OnDestroy} from '@angular/core';
import {Subscription} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {PoolConfigService} from "../../services/pool-config.service";
import {EventService} from "../../services/events.service";
import {SwimmingEvent} from "../../domain/swimming-event";
import {InscriptionsService} from "../../services/inscriptions.service";
import {ManualTimeService} from "../../services/manual.time.service";
import {NgForOf, NgIf} from "@angular/common";
import {ManualTimePipe} from "../../pipes/manual-time.pipe";

@Component({
  selector: 'app-print-event',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    ManualTimePipe
  ],
  templateUrl: './print-event.component.html',
  styleUrl: './print-event.component.css'
})
export class PrintEventComponent implements OnDestroy {
  private subscription: Subscription = new Subscription();
  public eventId: number = 0;
  public event: SwimmingEvent = {heats: -1, id: -1, description: "Not found",time:"99:99",date:"99/99/9999",intermediates:-1};
  public heats: Array<Array<Time>> = [];

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(route: ActivatedRoute,
              private poolConfigService: PoolConfigService,
              private eventService: EventService,
              private inscriptionService: InscriptionsService,
              private timeService: ManualTimeService) {
    this.subscription.add(route.params.subscribe(params => {
      this.eventId = +params['event']; // (+) converts string 'id' to a number
      this.loadEvent();
    }));
  }

  private loadEvent() {
    this.subscription.add(
      this.eventService.get(this.eventId).subscribe(event => {
        this.event = event;
        this.heats = [];
        this.loadConfig();
      })
    );
  }

  private loadInscriptions(eventId: number, heat: number) {
      this.subscription.add(
        this.inscriptionService.getByEventAndHeat(eventId,heat).subscribe(
          inscriptions=>inscriptions.forEach(i=>{
            this.heats[heat-1].forEach(time=>{
              if(time.lane==i.lane){
                time.name=i.name;
              }
            })
          })
        )
      );
  }

  private loadTimes(eventId: number, heat: number) {
      this.subscription.add(
        this.timeService.getByEventAndHeat(eventId,heat).subscribe(
          times=>times.forEach(t=>{
            this.heats[heat-1].forEach(time=>{
              if(time.lane==t.lane){
                time.time=t.time;
              }
            })
          })
        )
      );
  }

  private loadConfig() {
    this.subscription.add(
      this.poolConfigService.get().subscribe(config => {
        for (let heat = 1; heat <= this.event.heats; heat++) {
          const times:Array<Time>=[];
          config.lanes.forEach(lane=>
            times.push({lane})
          )
          this.heats.push(times);
          this.loadInscriptions(this.eventId,heat);
          this.loadTimes(this.eventId,heat);
        }
      })
    );
  }
}

interface Time {
  lane: number;
  name?: string;
  time?: string;
}
