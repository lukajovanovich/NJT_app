import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { API_BASE_URL, APP_NAME } from '../../app/app.constants';
import { AuthResponse, saveAuth } from '../../app/auth-storage';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class LoginPage {
  readonly appName = APP_NAME;

  email = '';
  sifra = '';
  errorMessage = '';
  loading = false;
  showRegisterForm = false;

  newUser = {
    ime: '',
    prezime: '',
    email: '',
    sifra: '',
    brojTelefona: '',
    uo: false,
    teamLeader: false,
  };

  constructor(private router: Router, private http: HttpClient) {}

  login() {
    this.errorMessage = '';

    if (!this.email.includes('@')) {
      this.errorMessage = 'Email mora sadržati znak @.';
      return;
    }

    this.loading = true;
    this.http.post<AuthResponse>(`${API_BASE_URL}/api/clan-organizacije/login`, { email: this.email, sifra: this.sifra }).subscribe({
      next: (response) => {
        saveAuth(response);
        const user = response.korisnik;

        if (user.uo) {
          this.router.navigate(['/uo']);
        } else if (user.teamLeader) {
          this.router.navigate(['/lider']);
        } else {
          this.router.navigate(['/pocetna']);
        }
      },
      error: (err) => {
        console.error('Greška prilikom logina:', err);
        this.loading = false;
        this.errorMessage = err.status === 401 ? 'Pogrešan email ili šifra.' : 'Greška na serveru. Pokušajte ponovo.';
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  register() {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!#%]).{8,}$/;
    if (!passwordRegex.test(this.newUser.sifra)) {
      alert('Šifra mora imati najmanje 8 karaktera, veliko i malo slovo, broj i znak !, # ili %.');
      return;
    }

    this.http.post(`${API_BASE_URL}/api/clan-organizacije`, this.newUser).subscribe({
      next: () => {
        alert(`Korisnik ${this.newUser.ime} ${this.newUser.prezime} je uspešno registrovan.`);
        this.showRegisterForm = false;
        this.newUser = {
          ime: '',
          prezime: '',
          email: '',
          sifra: '',
          brojTelefona: '',
          uo: false,
          teamLeader: false,
        };
      },
      error: (err) => {
        console.error('Greška pri registraciji:', err);
        const poruka = err.status === 409 ? 'Korisnik sa tim emailom već postoji.' : 'Došlo je do greške prilikom registracije.';
        alert(poruka);
      },
    });
  }
}
