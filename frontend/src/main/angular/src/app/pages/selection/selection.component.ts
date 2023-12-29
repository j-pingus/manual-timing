import { Component } from '@angular/core';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatChipListboxChange, MatChipsModule} from "@angular/material/chips";
import {FormsModule} from "@angular/forms";
import {JsonPipe, NgIf} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";

@Component({
  selector: 'app-selection',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    FormsModule,
    JsonPipe,
    MatIconModule,
    MatButtonModule,
    NgIf,
    MatTooltipModule
  ],
  templateUrl: './selection.component.html',
  styleUrl: './selection.component.css'
})
export class SelectionComponent {
  protected data={
    name:'',
    role:''
  };
  roleSelected($event: MatChipListboxChange) {
    this.data.role=$event.value;
    console.log(this.data);
  }

  nameProvided($event: Event) {
    console.log($event)
  }
}
