import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { APP_NAME } from '../../app/app.constants';
import { clearAuth, getCurrentUser, isAdmin, isTeamLeader } from '../../app/auth-storage';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar implements OnInit {
  @Output() logoutEvent = new EventEmitter<void>();

  readonly appName = APP_NAME;
  userName = '';
  isLoggedIn = false;
  canOpenLeader = false;
  canOpenAdmin = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.refreshState();
    this.router.events.subscribe(() => this.refreshState());
  }

  refreshState() {
    const user = getCurrentUser();
    this.isLoggedIn = !!user;
    this.userName = user?.ime || '';
    this.canOpenLeader = isTeamLeader(user) || isAdmin(user);
    this.canOpenAdmin = isAdmin(user);
  }

  onLogout() {
    clearAuth();
    this.refreshState();
    this.logoutEvent.emit();
    this.router.navigate(['/login']);
  }

  goHome() {
    const user = getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    if (user.uo) {
      this.router.navigate(['/uo']);
      return;
    }

    this.router.navigate(['/pocetna']);
  }

  openLeader() {
    this.router.navigate(['/lider']);
  }

  openAdmin() {
    this.router.navigate(['/uo']);
  }
}
