import { Injectable } from '@angular/core';
import EventBus from 'vertx3-eventbus-client';
@Injectable({
  providedIn: 'root'
})
export class ServerMessageService {

  constructor() {
    const eb = new EventBus('toto');

  }
}
