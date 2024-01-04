import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ManualTime} from "../domain/manual-time";

@Injectable({
    providedIn: 'root'
})
export class ManualTimeService {
    private static SERVICE_URL = 'api/time'

    constructor(private http: HttpClient) {
    }

    public save(data: ManualTime): Observable<string> {
        return this.http.post<string>(ManualTimeService.SERVICE_URL, data);
    }

    public getByEventAndLane(event: number, lane: number): Observable<Array<ManualTime>> {
        return this.http.get<Array<ManualTime>>(ManualTimeService.SERVICE_URL + 's/' + event + '/lane/' + lane);
    }
    public getByEventAndHeat(event: number, heat: number): Observable<Array<ManualTime>> {
        return this.http.get<Array<ManualTime>>(ManualTimeService.SERVICE_URL + 's/' + event + '/heat/' + heat);
    }
}
