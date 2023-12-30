export interface EventBusMessage {
    type:string;
    address:string;
    body:TimingMessage;
}
export interface TimingMessage {
    action: TimingAction;
    body:any;
}
export enum TimingAction {
    LOGIN='LOGIN'
}
