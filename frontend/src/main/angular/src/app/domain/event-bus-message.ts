export interface EventBusMessage {
    type: string;
    address: string;
    body: TimingMessage;
}

export interface TimingMessage {
    action: TimingAction;
    body: any;
    eventId: number;
    heatId: number;
    laneId: number;
    distance: number;
}

export enum TimingAction {
    LOGIN = 'LOGIN',
    USER_MOVE = 'USER_MOVE',
    REFRESH = 'REFRESH',
    TIME_SAVED = 'TIME_SAVED'
}
