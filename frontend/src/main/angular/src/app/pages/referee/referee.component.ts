import {Component, OnDestroy} from '@angular/core';
import {BackendMessageService} from "../../services/backend-message.service";
import {PoolConfigService} from "../../services/pool-config.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-referee',
  standalone: true,
  imports: [],
  templateUrl: './referee.component.html',
  styleUrl: './referee.component.css'
})
export class RefereeComponent implements OnDestroy{
  private subscription:Subscription;
  constructor(private messageService:BackendMessageService,private config:PoolConfigService) {
    this.subscription= messageService.subscribe(message=>
        //We can react from messages coming from server
        console.log('referee got message ',message));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }


    poolConfig() {
        this.config.get().subscribe(data=>console.log('got config',data));
    }
}
