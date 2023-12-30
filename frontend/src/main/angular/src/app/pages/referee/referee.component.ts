import { Component } from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";

@Component({
  selector: 'app-referee',
  standalone: true,
  imports: [],
  templateUrl: './referee.component.html',
  styleUrl: './referee.component.css'
})
export class RefereeComponent {
  constructor(private service:BackendMessageService) {
  }
}
