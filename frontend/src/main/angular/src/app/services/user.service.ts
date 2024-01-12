import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../domain/user";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private static SERVICE_URL = 'api/user'

  constructor(private http: HttpClient) {
  }

  getByLane(lane: number): Observable<Array<User>> {
    return this.http.get<Array<User>>(UserService.SERVICE_URL + 's/lane/' + lane);
  }

  save(request: User): Observable<User> {
    return this.http.post<User>(UserService.SERVICE_URL, request);
  }
}
