import {Directive, ElementRef, HostListener, OnInit} from '@angular/core';
import {ManualTimePipe} from "../pipes/manual-time.pipe";

@Directive({
    selector: '[appManualTime]',
    standalone: true
})
export class ManualTimeDirective implements OnInit{
    private el: HTMLInputElement;
    private manualTimePipe: ManualTimePipe;

    constructor(private elementRef: ElementRef
    ) {
        this.el = this.elementRef.nativeElement;
        this.manualTimePipe = new ManualTimePipe();
    }

    ngOnInit(): void {
        this.el.value = this.manualTimePipe.transform(this.el.value);
    }

    @HostListener("keyup", ["$event.target.value"])
    onKeyUp(value: string) {
        this.el.value = this.manualTimePipe.transform(value);
    }
}
