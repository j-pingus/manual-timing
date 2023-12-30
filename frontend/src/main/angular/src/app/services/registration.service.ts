import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {RegistrationRequest} from "../domain/registration-request";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private static SERVICE_URL = 'api/registration'

  constructor(private http: HttpClient) {
  }

  register(request: RegistrationRequest): Observable<string> {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');

    return this.http.post<string>(RegistrationService.SERVICE_URL, request,{headers, responseType:'text' as 'json'});
  }
}
