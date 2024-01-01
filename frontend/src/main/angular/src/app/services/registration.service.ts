import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../domain/user";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RegistrationService {
    private static SERVICE_URL = 'api/registration'

    constructor(private http: HttpClient) {
    }

    change(request: User): Observable<any> {
        return this.http.put(RegistrationService.SERVICE_URL, request);
    }

    getByLane(lane: number): Observable<Array<User>> {
        return this.http.get<Array<User>>(RegistrationService.SERVICE_URL + 's/lane/' + lane);
    }

    register(request: User): Observable<string> {
        const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');

        return this.http.post<string>(RegistrationService.SERVICE_URL, request, {headers, responseType: 'text' as 'json'});
    }
}
