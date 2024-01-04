import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MeetManagerService {
  private static SERVICE_URL = 'api/meet-manager'

  constructor(private http: HttpClient) {
  }

  public importAll() {
    return this.http.get(MeetManagerService.SERVICE_URL + '/reload');
  }

  public importHeat(event: number, heat: number) {
    return this.http.get(MeetManagerService.SERVICE_URL + '/reloadheat/' + event + '/' + heat);
  }
}
