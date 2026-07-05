import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';
import { Navbar } from '../components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar],
  template: `
    <app-navbar (logoutEvent)="logout()"></app-navbar>
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class App {
  constructor(private router: Router) {}

  logout() {
    // ovde samo preusmeravanje na login
    this.router.navigate(['/']);
  }
}
