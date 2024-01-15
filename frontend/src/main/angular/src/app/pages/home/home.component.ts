import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {ManualTimeService} from "../../services/manual.time.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, MatCardModule, MatIconModule, MatButtonModule, MatToolbarModule, MatTooltipModule, NgIf],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  public cacheSize: number=0;
  constructor(private router:Router,private saveTime:ManualTimeService) {
    saveTime.cacheSizeObservable().subscribe((size)=>this.cacheSize=size);
  }
  retrySaveTime(){
    this.saveTime.retryNow();
  }
  navigateHome() {
    this.router.navigate(['/']);
  }
}
