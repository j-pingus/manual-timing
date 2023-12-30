import { Component } from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";
import {PoolConfigService} from "../../services/pool-config.service";

@Component({
  selector: 'app-referee',
  standalone: true,
  imports: [],
  templateUrl: './referee.component.html',
  styleUrl: './referee.component.css'
})
export class RefereeComponent {
  constructor(private service:BackendMessageService,private config:PoolConfigService) {
  }

    poolConfig() {
        this.config.get().subscribe(data=>console.log('got config',data));
    }
}
