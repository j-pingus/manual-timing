import {TimeRecord} from "./time-record";

export interface TimerData {
  eventId: number;
  heatId: number;
  lane: number;
  times: Array<TimeRecord>;
  minimumDelay: number;
}
