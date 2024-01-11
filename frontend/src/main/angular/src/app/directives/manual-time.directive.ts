import {Directive, ElementRef} from '@angular/core';
import Cleave from "cleave.js";

@Directive({
  selector: '[appManualTime]',
  standalone: true
})
export class ManualTimeDirective {// implements OnInit{
  private el: HTMLInputElement;

  constructor(private elementRef: ElementRef
  ) {
    this.el = this.elementRef.nativeElement;
    new Cleave(this.el, {
      delimiters: [':', '.'],
      blocks: [2, 2, 3],
      numericOnly: true
    });
  }
}
