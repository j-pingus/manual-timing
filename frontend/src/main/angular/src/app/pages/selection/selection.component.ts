import {Component} from '@angular/core';
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
import {User} from "../../domain/user";
import {UserService} from "../../services/user.service";
import {Constants} from "../../Constants";
import {Router} from "@angular/router";
import {UserUtils} from "../../utils/user.utils";

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
export class SelectionComponent {
  public config$: Observable<PoolConfig>;
  public errorMessage = '';
  public needPassword = false;
  protected data: User = UserUtils.getSavedUser();

  constructor(poolConfigClient: PoolConfigService, private userService: UserService, private router: Router) {
    this.config$ = poolConfigClient.get();
  }

  laneSelected($event: MatChipListboxChange) {
    this.data.lane = $event.value;
  }

  register() {
    if (UserUtils.isRegistered()) {
      this.data.uuid = sessionStorage.getItem(Constants.USER_ID) as string;
      this.userService.save(this.data).subscribe((data) => {
        if (!data.uuid) {
          this.errorMessage = 'wrong password provided';
          console.log("logging out");
          UserUtils.logout();
          this.computeNeedPassword();
        } else {
          this.saveAndGo();
        }
      })
    } else {
      this.userService.save(this.data).subscribe((data) => {
        if (!data.uuid) {
          this.errorMessage = 'wrong password provided';
          this.computeNeedPassword();
        } else {
          UserUtils.saveUserId(data.uuid);
          this.saveAndGo()
        }
      })
    }
  }

  roleSelected($event: MatChipListboxChange) {
    if (this.data.role == 'control' && $event.value == 'referee') {
      console.log("logging out");
      UserUtils.logout();
    }
    this.data.role = $event.value;
    this.computeNeedPassword();
  }

  private saveAndGo() {
    this.data.uuid = undefined;
    this.data.password = undefined;
    UserUtils.saveUser(this.data);
    if (this.data.role == 'control') {
      this.router.navigate(['/' + this.data.role]);
    } else {
      this.router.navigate(['/' + this.data.role, 1]);
    }
  }

  private computeNeedPassword() {
    this.needPassword = (!UserUtils.isRegistered()) && this.data.role == 'referee';
  }
}
