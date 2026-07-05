import { Routes } from '@angular/router';
import { LoginPage } from '../pages/login/login';
import { Pocetna } from '../pages/pocetna/pocetna';
import { UoPage } from '../pages/uo/uo';
import { Prijava } from '../pages/prijava/prijava';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // default redirekt na login
  { path: 'login', component: LoginPage },
  { path: 'pocetna', component: Pocetna },
  { path: 'uo', component: UoPage },
  { path: 'prijava/:projectName/:projectId', component: Prijava },
];
