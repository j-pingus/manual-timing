import {Directive, ElementRef} from '@angular/core';
import Cleave from "cleave.js";

@Directive({
  selector: '[appManualTime]',
  standalone: true
})
export class ManualTimeDirective {// implements OnInit{
  constructor(private elementRef: ElementRef
  ) {
    //Type 'tel' allows for custom formatting but
    //somehow forces mobile device
    //to use the default numerical keypad
    if(this.elementRef.nativeElement.type!=='tel'){
      this.elementRef.nativeElement.type='tel';
    }
    new Cleave(this.elementRef.nativeElement, {
      delimiters: [':', '.'],
      blocks: [2, 2, 3],
      numericOnly: true
    });
  }
}
