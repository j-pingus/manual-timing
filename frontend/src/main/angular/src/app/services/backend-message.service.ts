import {Injectable} from '@angular/core';
import EventBus from '@vertx/eventbus-bridge-client.js';
import {EventBusMessage, TimingMessage} from "../domain/event-bus-message";
import {Subject, Subscription} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class BackendMessageService {
    private serverMessage: Subject<TimingMessage>;

    constructor() {
        const serverMessageSubject=new Subject<TimingMessage>();
        console.log('eventBus address:',environment.eventBus);
        const eventBus = new EventBus(environment.eventBus);
         eventBus.onopen = function () {
            eventBus.registerHandler('timing.message', (error: any, message: EventBusMessage) => {
                if (error) {
                    console.error('error:', error);
                }
                message.body.body = JSON.parse(message.body.body);
                console.log('message:', message);
                serverMessageSubject.next(message.body);
            });
        };
        this.serverMessage = serverMessageSubject;
    }
    subscribe(next:(value: TimingMessage) => void): Subscription{
        return this.serverMessage.subscribe(next);
    }
}
