import {Inscription} from './inscription';
import {TimeRecord} from "./time-record";
export interface Heat {
    id: number;
    inscription?: Inscription;
    times: Array<TimeRecord>;
}
