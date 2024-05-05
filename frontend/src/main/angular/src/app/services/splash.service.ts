import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class SplashService {
  private static SERVICE_URL = 'api/splash'

  constructor(private http: HttpClient) {
  }

  public refreshEvent(event: number) {
    return this.http.get(SplashService.SERVICE_URL + '/refresh/' + event);
  }

  public publishHeat(event: number, heat: number) {
    return this.http.get(SplashService.SERVICE_URL + '/publish/' + event + '/' + heat);
  }

}
