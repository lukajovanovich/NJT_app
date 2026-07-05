import { AfterViewInit, Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';
import { Navbar } from '../components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App implements AfterViewInit {
  @ViewChild('backgroundImage')
  private backgroundImage?: ElementRef<HTMLImageElement>;

  backgroundShift = '0px';

  constructor(private router: Router) {}

  ngAfterViewInit() {
    this.updateBackgroundShift();
  }

  @HostListener('window:scroll')
  onWindowScroll() {
    this.updateBackgroundShift();
  }

  @HostListener('window:resize')
  onWindowResize() {
    this.updateBackgroundShift();
  }

  logout() {
    // ovde samo preusmeravanje na login
    this.router.navigate(['/']);
  }

  updateBackgroundShift() {
    const imageHeight = this.backgroundImage?.nativeElement.getBoundingClientRect().height ?? 0;
    const viewportHeight = window.innerHeight;
    const maxShift = Math.max(imageHeight - viewportHeight, 0);
    const desiredShift = window.scrollY * 0.22;

    this.backgroundShift = `${-Math.min(desiredShift, maxShift)}px`;
  }
}
