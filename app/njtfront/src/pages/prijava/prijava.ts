import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

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
  imports: [CommonModule, HttpClientModule],
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
    this.http.get<BackendTeam[]>('http://localhost:8080/api/tim').subscribe({
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

    const currentUserName = localStorage.getItem('currentUserName') || 'Nepoznat';
    const parts = currentUserName.trim().split(' ');
    const ime = currentUser.ime || parts[0] || 'Nepoznato';
    const prezime = currentUser.prezime || (parts.length > 1 ? parts.slice(1).join(' ') : 'Nepoznato');

    const mojePrijave: any[] = JSON.parse(localStorage.getItem('mojiProjekti') || '[]');
    const noviId = mojePrijave.length === 0 ? 1 : Math.max(...mojePrijave.map((project) => project.id)) + 1;

    const vecPostoji = mojePrijave.some(
      (project) => project.clanId === this.clanId && project.teamId === team.id && project.status !== 'Odbijen'
    );
    if (vecPostoji) {
      alert('Vec imate prijavu za ovaj tim.');
      return;
    }

    mojePrijave.push({
      id: noviId,
      naziv: this.projectName,
      tim: team.naziv,
      teamId: team.id,
      status: 'Prijavljen',
      clanId: this.clanId,
      clanoviTima: [
        {
          ime,
          prezime,
          email: currentUser.email || 'N/A',
          brojTelefona: currentUser.brojTelefona || 'N/A',
        },
      ],
    });

    localStorage.setItem('mojiProjekti', JSON.stringify(mojePrijave));
    alert(`Prijavili ste se za ${team.naziv} tim na projektu ${this.projectName}!`);
    this.router.navigate(['/pocetna']);
  }
}
