import {Injectable} from '@angular/core';
import EventBus from '@vertx/eventbus-bridge-client.js';
import {EventBusMessage, TimingMessage} from "../domain/event-bus-message";
import {Subject, Subscription} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class BackendMessageService {
    private serverMessage: Subject<TimingMessage>;

    constructor() {
        const serverMessageSubject=new Subject<TimingMessage>();
        const eventBus = new EventBus('http://localhost:8765/api/eventbus');
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
