import { Routes } from '@angular/router';
import { LoginPage } from '../pages/login/login';
import { Pocetna } from '../pages/pocetna/pocetna';
import { UoPage } from '../pages/uo/uo';
import { Prijava } from '../pages/prijava/prijava';
import { LiderPage } from '../pages/lider/lider';
import { adminGuard, authGuard, leaderGuard } from './auth.guards';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginPage },
  { path: 'pocetna', component: Pocetna, canActivate: [authGuard] },
  { path: 'lider', component: LiderPage, canActivate: [leaderGuard] },
  { path: 'uo', component: UoPage, canActivate: [adminGuard] },
  { path: 'prijava/:projectName/:projectId', component: Prijava, canActivate: [authGuard] },
];
