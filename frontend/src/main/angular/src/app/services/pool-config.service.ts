import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PoolConfig} from "../domain/pool-config";

@Injectable({
  deps: [HttpClient],
  providedIn: 'root'
})
export class PoolConfigService {
  private static SERVICE_URL = 'api/poolconfig'

  constructor(private http: HttpClient) {
  }

  get(): Observable<PoolConfig> {
    return this.http.get<PoolConfig>(PoolConfigService.SERVICE_URL);
  }
}
