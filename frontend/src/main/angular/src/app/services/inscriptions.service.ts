import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Inscription} from "../domain/inscription";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class InscriptionsService {
  private static SERVICE_URL = 'api/inscription'

  constructor(private http: HttpClient) {
  }
  public save(data:Inscription):Observable<string>{
    return this.http.post<string>(InscriptionsService.SERVICE_URL,data);
  }
  public getByEventAndLane(event:number,lane:number):Observable<Array<Inscription>>{
    return this.http.get<Array<Inscription>>(InscriptionsService.SERVICE_URL+'s/'+event+'/lane/'+lane);
  }
  public getByEventAndHeat(event:number,heat:number):Observable<Array<Inscription>>{
    return this.http.get<Array<Inscription>>(InscriptionsService.SERVICE_URL+'s/'+event+'/heat/'+heat);
  }
}
