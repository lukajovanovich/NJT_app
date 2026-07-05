import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar implements OnInit {
  @Output() logoutEvent = new EventEmitter<void>();

  userName = '';
  isLoggedIn = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.checkLogin();

    this.router.events.subscribe(() => {
      this.checkLogin();
    });
  }

  checkLogin() {
    const fullName = localStorage.getItem('currentUserName') || '';
    this.userName = fullName.split(' ')[0] || fullName;
    this.isLoggedIn = !!fullName;
  }

  onLogout() {
    this.router.navigate(['/login']).then(() => {
      localStorage.removeItem('currentUserId');
      localStorage.removeItem('currentUserName');
      localStorage.removeItem('currentUser');
      this.userName = '';
      this.isLoggedIn = false;
      this.logoutEvent.emit();
    });
  }

  goHome() {
    if (this.isLoggedIn) {
      this.router.navigate(['/pocetna']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
