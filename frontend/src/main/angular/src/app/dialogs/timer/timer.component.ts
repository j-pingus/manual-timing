import {Component, Inject} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TimerData} from "../../domain/timer-data";
import {ManualTime} from "../../domain/manual-time";
import {ManualTimeService} from "../../services/manual.time.service";
import {BackendMessageService} from "../../services/backend-message.service";
import {TimingAction} from "../../domain/event-bus-message";

@Component({
  selector: 'app-timer',
  standalone: true,
  imports: [
    MatButtonModule
  ],
  templateUrl: './timer.component.html',
  styleUrl: './timer.component.css'
})
export class TimerComponent {

  buttonText = "Start!";
  timerText = '00:00.00';
  interval: any = undefined;
  startTimer = 0;
  lastRecord = 0;

  constructor(private dialog: MatDialogRef<TimerComponent>,
              @Inject(MAT_DIALOG_DATA) private timerData: TimerData,
              private manualTimeService: ManualTimeService,
              backendMessage: BackendMessageService) {
    dialog.disableClose = true;
    backendMessage.subscribe((message) => {
      if (message.eventId == timerData.eventId &&
        message.heatId == timerData.heatId &&
        message.laneId == timerData.lane &&
        message.action == TimingAction.REFRESH_TIMES) {
        const index = timerData.times.findIndex(t => t.distance == message.distance);
        if (index != -1) {
          this.removeTime(index);
        }
      }
    });
  }

  private setTextWithDistance() {
    this.buttonText = 'next: ' + this.timerData.times[0].distance + 'm'
  }

  buttonPushed() {
    const pressedTS = Date.now();
    //Start the timer
    if (this.startTimer == 0) {
      this.startTimer = pressedTS;
      this.lastRecord = pressedTS;
      this.interval = setInterval(() => this.updateTimerText(), 100);
      this.setTextWithDistance();
      return;
    }
    //If double click...
    if ((pressedTS - this.lastRecord) < this.timerData.minimumDelay) {
      this.buttonText = "!!  too early  !!";
      setTimeout(() => this.setTextWithDistance(), 1200);
      return;
    }
    this.saveTime(pressedTS);
  }

  private updateTimerText() {
    var ellapsed = Date.now() - this.startTimer;
    this.timerText = this.millisToText(ellapsed);
  }

  private millisToText(ellapsed: number): string {
    var millis = ellapsed % 1000;
    var sec = Math.floor(ellapsed / 1000) % 60;
    var min = Math.floor(ellapsed / 60000);
    return this.pad(min, 2) + ':' + this.pad(sec, 2) + '.' + this.pad(millis, 3);
  }

  private pad(number: number, paddingZeroes: number) {
    return ('00000' + number).slice(-paddingZeroes);
  }

  private saveTime(pressedTS: number) {
    const ellapsed = pressedTS - this.startTimer;
    this.lastRecord = pressedTS;
    const time: ManualTime = {
      time: this.millisToText(ellapsed),
      distance: this.timerData.times[0].distance,
      heat: this.timerData.heatId,
      lane: this.timerData.lane,
      event: this.timerData.eventId
    };
    this.manualTimeService.save(time).subscribe();
    this.removeTime(0);
  }

  private removeTime(index: number) {
    this.timerData.times.splice(index, 1);
    if (this.timerData.times.length == 0) {
      clearInterval(this.interval);
      this.dialog.close();
    }
    this.setTextWithDistance();
  }

  public skip() {
    if (this.startTimer == 0) {
      this.dialog.close();
    } else {
      this.removeTime(0);
    }
  }
}
