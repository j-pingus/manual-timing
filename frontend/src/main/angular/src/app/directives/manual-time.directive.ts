import {Directive, ElementRef, HostListener} from '@angular/core';
import {ManualTimePipe} from "../pipes/manual-time.pipe";

@Directive({
    selector: '[appManualTime]',
    standalone: true
})
export class ManualTimeDirective {
    private el: HTMLInputElement;
    private manualTimePipe: ManualTimePipe;

    constructor(private elementRef: ElementRef
    ) {
        this.el = this.elementRef.nativeElement;
        this.manualTimePipe = new ManualTimePipe();
    }

    @HostListener("keyup", ["$event.target.value"])
    onChanged(value: string) {
        console.log("keydown",value);
        this.el.value = this.manualTimePipe.transform(value);
    }
}
