import { Component } from '@angular/core';
import {MeetManagerService} from "../../../services/meet-manager.service";

@Component({
  selector: 'app-meet-manager',
  standalone: true,
  imports: [],
  templateUrl: './meet-manager.component.html',
  styleUrl: './meet-manager.component.css'
})
export class MeetManagerComponent {
  constructor(private meetManagerService:MeetManagerService) {
  }
  public reload(){
    this.meetManagerService.importAll().subscribe();
  }
}
