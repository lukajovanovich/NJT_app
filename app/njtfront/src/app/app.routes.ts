import { Routes } from '@angular/router';
import { LoginPage } from '../pages/login/login';
import { Pocetna } from '../pages/pocetna/pocetna';
import { AdminPage } from '../pages/admin/admin';
import { Prijava } from '../pages/prijava/prijava';
import { RukovodilacPage } from '../pages/rukovodilac/rukovodilac';
import { adminGuard, authGuard, rukovodilacGuard } from './auth.guards';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginPage },
  { path: 'pocetna', component: Pocetna, canActivate: [authGuard] },
  { path: 'rukovodilac', component: RukovodilacPage, canActivate: [rukovodilacGuard] },
  { path: 'admin', component: AdminPage, canActivate: [adminGuard] },
  { path: 'prijava/:projectName/:projectId', component: Prijava, canActivate: [authGuard] },
];
