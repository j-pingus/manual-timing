import {Routes} from '@angular/router';
import {SelectionComponent} from "./pages/selection/selection.component";
import {RefereeComponent} from "./pages/referee/referee.component";
import {ControlComponent} from "./pages/control/control.component";
import {RefereesComponent} from "./pages/control/referees/referees.component";
import {RaceComponent} from "./pages/control/race/race.component";
import {PrintEventComponent} from "./pages/print-event/print-event.component";

export const routes: Routes = [
  {
    path: '',
    component: SelectionComponent
  },
  {
    path: 'referee/:id',
    component: RefereeComponent
  },
  {
    path: 'control',
    component: ControlComponent,
    children: [
      {
        path: 'referees',
        component: RefereesComponent
      },
      {
        path: 'race/:event/:heat',
        component: RaceComponent
      },
    ]
  },
  {
    path: 'print-event/:event',
    component: PrintEventComponent
  },
  {path: '**', component: SelectionComponent}
];
