import {Routes} from '@angular/router';
import {SelectionComponent} from "./pages/selection/selection.component";
import {RefereeComponent} from "./pages/referee/referee.component";
import {ControlComponent} from "./pages/control/control.component";

export const routes: Routes = [
  {
    path: '',
    component: SelectionComponent
  },
  {
    path: 'referee',
    component: RefereeComponent
  },
  {
    path: 'control',
    component: ControlComponent
  },
  { path: '**', component: SelectionComponent },
];
