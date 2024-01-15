import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, Subject, tap} from "rxjs";
import {ManualTime} from "../domain/manual-time";

@Injectable({
  providedIn: 'root'
})
export class ManualTimeService {
  private static SERVICE_URL = 'api/time'
  private static CACHE_KEY = "timing.save.cache";
  private cacheSize: Subject<number>;
  private saveCache: Array<ManualTime> = [];

  constructor(private http: HttpClient) {
    this.cacheSize = new Subject();
    this.retryNow();
  }

  public cacheSizeObservable(): Observable<number> {
    return this.cacheSize.asObservable();
  }

  public getByEventAndHeat(event: number, heat: number): Observable<Array<ManualTime>> {
    return this.http.get<Array<ManualTime>>(ManualTimeService.SERVICE_URL + 's/' + event + '/heat/' + heat);
  }

  public getByEventAndLane(event: number, lane: number): Observable<Array<ManualTime>> {
    return this.http.get<Array<ManualTime>>(ManualTimeService.SERVICE_URL + 's/' + event + '/lane/' + lane);
  }

  public retryNow() {
    this.saveCache = [];
    const cache = localStorage.getItem(ManualTimeService.CACHE_KEY);
    if (cache) {
      const fromStore: Array<ManualTime> = JSON.parse(cache);
      fromStore.forEach(cached => this.save(cached).subscribe());
    }
  }

  public save(data: ManualTime): Observable<string> {
    this.pushCache(data);
    return this.http.post<string>(ManualTimeService.SERVICE_URL, data, {responseType: 'text' as 'json'})
      .pipe(tap(() =>
        this.popCache(data)
      ));
  }

  private copyToLocal() {
    console.log("save cache:", this.saveCache);
    this.cacheSize.next(this.saveCache.length);
    localStorage.setItem(ManualTimeService.CACHE_KEY, JSON.stringify(this.saveCache));
  }

  private pushCache(data: ManualTime) {
    this.saveCache.push(data);
    this.copyToLocal();
  }

  private popCache(data: ManualTime) {
    this.saveCache = this.saveCache.filter(cache => {
      return cache.event != data.event ||
        cache.time != data.time ||
        cache.lane != data.lane ||
        cache.heat != data.heat;
    });
    this.copyToLocal();
  }
}
