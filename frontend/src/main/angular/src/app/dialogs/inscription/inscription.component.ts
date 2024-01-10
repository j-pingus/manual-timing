import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog";
import {Inscription} from "../../domain/inscription";
import {ManualTimePipe} from "../../pipes/manual-time.pipe";
import {MatButtonModule} from "@angular/material/button";
import {CountryUtils} from "../../utils/country.utils";

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [
    ManualTimePipe,
    MatDialogTitle,
    MatDialogContent,
    MatButtonModule
  ],
  templateUrl: './inscription.component.html',
  styleUrl: './inscription.component.css'
})
export class InscriptionComponent {
  public flag:string='lu';
  constructor(
    private dialog:MatDialogRef<InscriptionComponent>,
    @Inject(MAT_DIALOG_DATA)public inscription:Inscription) {
    this.flag=CountryUtils.countryCodeIso2(this.inscription.nation);
  }
  public close(){
    this.dialog.close();
  }
}
