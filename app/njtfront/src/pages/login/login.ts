// Uvoz osnovnih Angular modula i servisa
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

// Interfejs koji opisuje strukturu objekta Koordinator
interface Koordinator {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  sifra: string;
  uo: boolean;
  brojTelefona: string;
}

// Interfejs koji opisuje strukturu Projekta
interface Project {
  id: number;
  naziv: string;
  datumPocetka: string;
  datumZavrsetka: string;
  odabraniKoordinator: Koordinator;
}

// Dekorator koji označava Angular komponentu
@Component({
  selector: 'app-login', // naziv komponente koji se koristi u HTML-u
  standalone: true, // omogućava korišćenje bez modula
  imports: [CommonModule, FormsModule, HttpClientModule], // potrebni moduli za formu i HTTP
  templateUrl: './login.html', // putanja do HTML šablona
  styleUrls: ['./login.css'] // putanja do CSS fajla
})
export class LoginPage {
  // Polja za login
  email: string = '';
  sifra: string = '';
  errorMessage: string = '';

  // Lista projekata (učitava se sa backend-a)
  projects: Project[] = [];
  loading: boolean = true;

  // Polja za prikaz i unos registracije
  showRegisterForm: boolean = false;
  newUser: any = {
    ime: '',
    prezime: '',
    email: '',
    sifra: '',
    brojTelefona: '',
    uo: false
  };

  // Konstruktor injektuje ruter i HTTP servis
  constructor(private router: Router, private http: HttpClient) {}

  // Metoda koja se izvršava pri pokretanju komponente
  ngOnInit() {
    // GET zahtev ka backendu za učitavanje projekata
    this.http.get<Project[]>('http://localhost:8080/api/projekat').subscribe({
      next: data => {
        this.projects = data; // dodela dobijenih podataka
        this.loading = false; // završeno učitavanje
      },
      error: err => {
        console.error("Greška pri učitavanju projekata:", err);
        this.loading = false;
        this.errorMessage = 'Ne mogu da učitam podatke sa servera.';
      }
    });
  }

  // Login funkcija
  login() {
    this.errorMessage = '';

    // Provera validnosti email-a
    if (!this.email.includes('@')) {
      this.errorMessage = 'Email mora sadržati znak @';
      return;
    }

    // Priprema JSON objekta za slanje backendu
    const loginData = { email: this.email, sifra: this.sifra };

    // POST zahtev ka backendu za login
    this.http.post<any>('http://localhost:8080/api/clan-organizacije/login', loginData).subscribe({
      next: (user) => {
        if (user) {
          // Čuvanje korisnika u localStorage
          localStorage.setItem('currentUserId', user.id.toString());
          localStorage.setItem('currentUserName', `${user.ime} ${user.prezime}`);
          localStorage.setItem('currentUser', JSON.stringify(user));

          // Navigacija na odgovarajuću stranicu (uo = upravni odbor)
          this.router.navigate([user.uo ? '/uo' : '/pocetna']);
        } else {
          this.errorMessage = 'Pogrešan email ili šifra.';
        }
      },
      error: (err) => {
        console.error('Greška prilikom logina:', err);
        // Prikaz greške u zavisnosti od statusa
        this.errorMessage =
          err.status === 401
            ? 'Pogrešan email ili šifra.'
            : 'Greška na serveru. Pokušajte ponovo.';
      },
    });
  }

  // Registracija novog korisnika
  register() {
    // Provera šifre regex-om
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!#%]).{8,}$/;
    if (!passwordRegex.test(this.newUser.sifra)) {
      alert('Šifra mora imati najmanje 8 karaktera, bar jedno veliko i malo slovo, jedan broj i znak (!, #, %).');
      return;
    }

    // Backend endpoint za registraciju
    const apiUrl = 'http://localhost:8080/api/clan-organizacije';

    // Kreiranje JSON objekta koji se šalje serveru
    const korisnikPayload = {
      ime: this.newUser.ime,
      prezime: this.newUser.prezime,
      email: this.newUser.email,
      brojTelefona: this.newUser.brojTelefona,
      sifra: this.newUser.sifra,
      uo: this.newUser.uo
    };

    // Slanje POST zahteva ka backendu
    this.http.post(apiUrl, korisnikPayload).subscribe({
      next: (res) => {
        console.log('✅ Registracija uspešna:', res);
        alert(`Korisnik ${this.newUser.ime} ${this.newUser.prezime} je uspešno registrovan!`);
        // Reset forme i zatvaranje
        this.showRegisterForm = false;
        this.newUser = { ime: '', prezime: '', email: '', sifra: '', brojTelefona: '', uo: false };
      },
      error: (err) => {
        console.error('❌ Greška pri registraciji:', err);
        alert('Došlo je do greške prilikom registracije. Pokušajte ponovo.');
      }
    });
  }
}
