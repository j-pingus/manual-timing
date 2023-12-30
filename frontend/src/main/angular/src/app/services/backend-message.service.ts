import {Injectable} from '@angular/core';
import EventBus from '@vertx/eventbus-bridge-client.js';
import {EventBusMessage} from "../domain/event-bus-message";

@Injectable({
    providedIn: 'root'
})
export class BackendMessageService {

    constructor() {
        const eventBus = new EventBus('http://localhost:8765/api/eventbus');
        eventBus.onopen = function () {
            eventBus.registerHandler('timing.message', (error: any, message: EventBusMessage) => {
                if (error) {
                    console.error('error:', error);
                }
                message.body.body = JSON.parse(message.body.body);
                console.log('message:', message);
            });
        };
    }
}
