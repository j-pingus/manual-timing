import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SwimmingEvent} from "../domain/swimming-event";
@Injectable({
  deps: [HttpClient],
  providedIn: 'root'
})
export class EventService {
  private static SERVICE_URL = 'api/event'

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Array<SwimmingEvent>> {
    return this.http.get<Array<SwimmingEvent>>(EventService.SERVICE_URL+'s');
  }

  save(event:SwimmingEvent): Observable<string> {
    return this.http.post<string>(EventService.SERVICE_URL,event);
  }
}
