import {Directive, ElementRef} from '@angular/core';
import Cleave from "cleave.js";

@Directive({
  selector: '[appManualTime]',
  standalone: true
})
export class ManualTimeDirective {// implements OnInit{
  constructor(private elementRef: ElementRef
  ) {
    new Cleave(this.elementRef.nativeElement, {
      delimiters: [':', '.'],
      blocks: [2, 2, 3],
      //numericOnly: true,
      numeralPositiveOnly:true
    });
  }
}
