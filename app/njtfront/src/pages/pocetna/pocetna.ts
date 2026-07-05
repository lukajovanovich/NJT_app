import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

interface Member {
  ime: string;
  prezime: string;
  email: string;
  brojTelefona: string;
}

interface UserProject {
  id: number;
  naziv: string;
  tim: string;
  status: string;
  clanId: number;
  teamId?: number;
  clanoviTima: Member[];
}

interface BackendUserProject {
  id: number;
  clanId: number;
  timId: number;
  naziv: string;
  tim: string;
  status: string;
  clanoviTima?: Member[];
}

interface Project {
  id: number;
  naziv: string;
  datumPocetka: string;
  datumZavrsetka: string;
  odabraniKoordinator: {
    id: number;
    ime: string;
    prezime: string;
    email: string;
    brojTelefona: string;
  };
}

@Component({
  selector: 'app-pocetna',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './pocetna.html',
  styleUrls: ['./pocetna.css'],
})
export class Pocetna implements OnInit {
  users: UserProject[] = [];
  activeProjects: Project[] = [];
  selectedProject: UserProject | null = null;
  private currentUserId: number | null = null;

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const storedUserId = Number(localStorage.getItem('currentUserId'));
    if (!storedUserId) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUserId = storedUserId;
    this.loadActiveProjects();
    this.loadUserProjects();
  }

  loadActiveProjects() {
    this.http.get<Project[]>('http://localhost:8080/api/projekat').subscribe({
      next: (data) => {
        this.activeProjects = data.map((project) => ({
          ...project,
          datumPocetka: this.formatDate(project.datumPocetka),
          datumZavrsetka: this.formatDate(project.datumZavrsetka),
        }));
      },
      error: (err) => console.error('Greska pri ucitavanju aktivnih projekata:', err),
    });
  }

  loadUserProjects() {
    if (!this.currentUserId) {
      this.users = [];
      return;
    }

    const clanId = this.currentUserId;
    const lokalnePrijave: UserProject[] = JSON.parse(localStorage.getItem('mojiProjekti') || '[]').filter(
      (project: UserProject) => project.clanId === clanId
    );

    this.http.get<BackendUserProject[]>(`http://localhost:8080/api/clanTim/byClanDTO/${clanId}`).subscribe({
      next: (data) => {
        const prijaveIzBaze: UserProject[] = data.map((membership) => ({
          id: membership.id,
          naziv: membership.naziv || 'Nepoznat projekat',
          tim: membership.tim || 'Nepoznat tim',
          status: membership.status || 'Prihvacen',
          clanId: membership.clanId || clanId,
          teamId: membership.timId,
          clanoviTima: (membership.clanoviTima || []).map((member) => ({
            ime: member.ime || 'Nepoznato',
            prezime: member.prezime || '',
            email: member.email || '',
            brojTelefona: member.brojTelefona || '',
          })),
        }));

        const prihvaceniTimoviIzBaze = new Set(
          prijaveIzBaze
            .map((project) => project.teamId)
            .filter((teamId): teamId is number => teamId !== undefined)
        );

        const lokalneBezDuplikata = lokalnePrijave.filter(
          (project) =>
            !(this.isAccepted(project.status) && project.teamId !== undefined && prihvaceniTimoviIzBaze.has(project.teamId))
        );

        this.users = [...lokalneBezDuplikata, ...prijaveIzBaze];
      },
      error: () => {
        this.users = lokalnePrijave;
      },
    });
  }

  isAccepted(status: string): boolean {
    return this.normalizeStatus(status) === 'prihvacen';
  }

  private normalizeStatus(status: string): string {
    return status
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase();
  }

  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('sr-RS');
  }

  handleRowClick(user: UserProject) {
    if (!this.isAccepted(user.status)) {
      return;
    }

    this.selectedProject = this.selectedProject?.id === user.id ? null : user;
  }

  getSelectedTeamMembers(): Member[] {
    return this.selectedProject ? this.selectedProject.clanoviTima || [] : [];
  }

  openPrijava(project: Project) {
    this.router.navigate(['/prijava', project.naziv, project.id]);
  }
}
