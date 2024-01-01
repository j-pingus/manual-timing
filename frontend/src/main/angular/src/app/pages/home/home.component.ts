import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";

@Component({
  selector: 'app-home',
  standalone: true,
    imports: [RouterOutlet, MatCardModule, MatIconModule, MatButtonModule, MatToolbarModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private router:Router) {
  }
  navigateHome() {
    this.router.navigate(['/']);
  }
}
