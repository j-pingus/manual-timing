import {Component, OnInit} from '@angular/core';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatChipListboxChange, MatChipsModule} from "@angular/material/chips";
import {FormsModule} from "@angular/forms";
import {AsyncPipe, JsonPipe, NgForOf, NgIf} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import {PoolConfigService} from "../../services/pool-config.service";
import {Observable} from "rxjs";
import {PoolConfig} from "../../domain/pool-config";
import {RegistrationRequest} from "../../domain/registration-request";
import {RegistrationService} from "../../services/registration.service";
import {Constants} from "../../Constants";
import {Router} from "@angular/router";

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
    MatTooltipModule,
    AsyncPipe,
    NgForOf
  ],
  templateUrl: './selection.component.html',
  styleUrl: './selection.component.css'
})
export class SelectionComponent implements OnInit {
  public config: Observable<PoolConfig>;
  protected data: RegistrationRequest = {};

  constructor(poolConfigClient: PoolConfigService, private registrationService: RegistrationService, private router: Router) {
    this.config = poolConfigClient.get();
  }

  laneSelected($event: MatChipListboxChange) {
    this.data.lane = $event.value;

  }

  ngOnInit(): void {
    const storageData = localStorage.getItem(Constants.USER_DATA);
    this.data = JSON.parse(storageData ? storageData : '{}') as RegistrationRequest;
  }

  register() {
    this.registrationService.register(this.data).subscribe((data) => {
      console.log(data);
      localStorage.clear();
      sessionStorage.clear();
      sessionStorage.setItem(Constants.USER_ID, data);
      localStorage.setItem(Constants.USER_DATA, JSON.stringify(this.data));
      this.router.navigate(['/' + this.data.role]);
    })
  }

  roleSelected($event: MatChipListboxChange) {
    this.data.role = $event.value;
    console.log(this.data);
  }
}
