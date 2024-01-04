import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-control',
  standalone: true,
    imports: [
        RouterLink,
        RouterOutlet,
        RouterLinkActive,
        MatToolbarModule,
        MatButtonModule
    ],
  templateUrl: './control.component.html',
  styleUrl: './control.component.css'
})
export class ControlComponent {

}
