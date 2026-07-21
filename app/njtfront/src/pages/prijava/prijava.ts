import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { API_BASE_URL } from '../../app/app.constants';

interface Team {
  id: number;
  naziv: string;
}

interface BackendTeam {
  id: number;
  naziv: string;
  projekat?: {
    id: number;
  };
}

@Component({
  selector: 'app-prijava',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './prijava.html',
  styleUrls: ['./prijava.css'],
})
export class Prijava implements OnInit {
  projectName: string | null = null;
  projectId: number | null = null;
  teams: Team[] = [];
  loadingTeams = true;
  clanId: number | null = null;

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) {
    this.route.paramMap.subscribe((params) => {
      this.projectName = params.get('projectName');
      const id = params.get('projectId');
      this.projectId = id ? +id : null;

      if (this.clanId) {
        this.loadTeams();
      }
    });
  }

  ngOnInit() {
    const storedUserId = Number(localStorage.getItem('currentUserId'));
    if (!storedUserId) {
      alert('Morate biti ulogovani da biste se prijavili na tim.');
      this.router.navigate(['/login']);
      return;
    }

    this.clanId = storedUserId;
    this.loadTeams();
  }

  private loadTeams() {
    if (!this.projectId) {
      this.loadingTeams = false;
      this.teams = [];
      return;
    }

    this.loadingTeams = true;
    this.http.get<BackendTeam[]>(`${API_BASE_URL}/api/tim`).subscribe({
      next: (data) => {
        this.teams = data
          .filter((team) => team.projekat?.id === this.projectId)
          .map((team) => ({
            id: team.id,
            naziv: team.naziv,
          }));
        this.loadingTeams = false;
      },
      error: (err) => {
        console.error('Greska pri ucitavanju timova:', err);
        this.loadingTeams = false;
        this.teams = [];
      },
    });
  }

  handleSignUp(team: Team) {
    if (!this.projectId || !this.clanId) {
      alert('Greska: projekat nije pronadjen.');
      return;
    }

    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    if (!currentUser?.id) {
      alert('Morate biti ulogovani da biste se prijavili na tim.');
      this.router.navigate(['/login']);
      return;
    }

    this.http
      .post(`${API_BASE_URL}/api/prijava`, {
        clanId: this.clanId,
        timId: team.id,
      })
      .subscribe({
        next: () => {
          alert(`Prijavili ste se za ${team.naziv} tim na projektu ${this.projectName}!`);
          this.router.navigate(['/pocetna']);
        },
        error: (err) => {
          console.error('Greska pri slanju prijave:', err);

          if (err.status === 409) {
            alert('Vec imate aktivnu prijavu za ovaj tim.');
            return;
          }

          alert('Doslo je do greske prilikom slanja prijave. Pokusajte ponovo.');
        },
      });
  }
}
