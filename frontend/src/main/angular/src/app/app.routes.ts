import {Routes} from '@angular/router';
import {SelectionComponent} from "./pages/selection/selection.component";
import {RefereeComponent} from "./pages/referee/referee.component";
import {ControlComponent} from "./pages/control/control.component";
import {RefereesComponent} from "./pages/control/referees/referees.component";
import {MeetManagerComponent} from "./pages/control/meet-manager/meet-manager.component";
import {RaceComponent} from "./pages/control/race/race.component";
import {PoolConfigComponent} from "./pages/control/pool-config/pool-config.component";

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
    children:[
      {
        path: 'referees',
        component: RefereesComponent
      },
      {
        path: 'meet-manager',
        component: MeetManagerComponent
      },
       {
        path: 'pool-config',
        component: PoolConfigComponent
      },
      {
        path: 'race/:event/:heat',
        component: RaceComponent
      },
    ]
  },
  { path: '**', component: SelectionComponent },
];
