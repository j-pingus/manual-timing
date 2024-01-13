import {Inscription} from "./inscription";
import {TimeRecord} from "./time-record";

export interface Lane {
  lane: number;
  inscription: Inscription;
  times: Array<TimeRecord>;
}
