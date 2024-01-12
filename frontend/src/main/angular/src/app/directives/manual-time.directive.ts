import {Directive, ElementRef} from '@angular/core';
import Cleave from "cleave.js";

@Directive({
  selector: 'input[appManualTime]',
  standalone: true
})
export class ManualTimeDirective {
  constructor(private elementRef: ElementRef<HTMLInputElement>
  ) {
    const nativeElement = elementRef.nativeElement;
    //Type 'tel' allows for custom formatting but
    //somehow forces mobile device
    //to use the default numerical keypad
    if(nativeElement.type!=='tel'){
      nativeElement.type='tel';
    }
    new Cleave(nativeElement, {
      delimiters: [':', '.'],
      blocks: [2, 2, 3],
      numericOnly: true
    });
  }
}
