import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../domain/user";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private static SERVICE_URL = 'api/registration'

  constructor(private http: HttpClient) {
  }

  getByLane(lane: number): Observable<Array<User>> {
    return this.http.get<Array<User>>(RegistrationService.SERVICE_URL + 's/lane/' + lane);
  }

  save(request: User): Observable<User> {
    return this.http.post<User>(RegistrationService.SERVICE_URL, request);
  }
}
