import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { API_BASE_URL, APP_NAME } from '../../app/app.constants';
import { getCurrentUser } from '../../app/auth-storage';

interface Member {
  id?: number;
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
  } | null;
}

interface Task {
  id: number;
  naslov: string;
  opis: string;
  status: string;
  projekatNaziv?: string | null;
  timNaziv?: string | null;
  kreirao?: Member;
  datumKreiranja?: string;
}

@Component({
  selector: 'app-pocetna',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pocetna.html',
  styleUrls: ['./pocetna.css'],
})
export class Pocetna implements OnInit {
  readonly appName = APP_NAME;

  users: UserProject[] = [];
  tasks: Task[] = [];
  activeProjects: Project[] = [];
  selectedProject: UserProject | null = null;
  private currentUserId: number | null = null;

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const currentUser = getCurrentUser();
    if (!currentUser?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUserId = currentUser.id;
    this.loadActiveProjects();
    this.loadUserProjects();
    this.loadTasks();
  }

  loadActiveProjects() {
    this.http.get<Project[]>(`${API_BASE_URL}/api/projekat`).subscribe({
      next: (data) => {
        this.activeProjects = data.map((project) => ({
          ...project,
          datumPocetka: this.formatDate(project.datumPocetka),
          datumZavrsetka: this.formatDate(project.datumZavrsetka),
        }));
      },
      error: (err) => console.error('Greška pri učitavanju aktivnih projekata:', err),
    });
  }

  loadUserProjects() {
    if (!this.currentUserId) {
      this.users = [];
      return;
    }

    this.http.get<BackendUserProject[]>(`${API_BASE_URL}/api/prijava/byClan/${this.currentUserId}`).subscribe({
      next: (data) => {
        this.users = data.map((membership) => ({
          id: membership.id,
          naziv: membership.naziv || 'Nepoznat projekat',
          tim: membership.tim || 'Nepoznat tim',
          status: membership.status || 'Prijavljen',
          clanId: membership.clanId || this.currentUserId!,
          teamId: membership.timId,
          clanoviTima: (membership.clanoviTima || []).map((member) => ({
            id: member.id,
            ime: member.ime || 'Nepoznato',
            prezime: member.prezime || '',
            email: member.email || '',
            brojTelefona: member.brojTelefona || '',
          })),
        }));
      },
      error: () => {
        this.users = [];
      },
    });
  }

  loadTasks() {
    this.http.get<Task[]>(`${API_BASE_URL}/api/zadatak/my/assigned`).subscribe({
      next: (data) => {
        this.tasks = data;
      },
      error: (err) => {
        console.error('Greška pri učitavanju zadataka:', err);
        this.tasks = [];
      },
    });
  }

  updateTaskStatus(task: Task, status: 'Uradjen' | 'Nije uradjen') {
    this.http.put<Task>(`${API_BASE_URL}/api/zadatak/my/${task.id}/status`, { status }).subscribe({
      next: (updatedTask) => {
        this.tasks = this.tasks.map((item) => (item.id === updatedTask.id ? updatedTask : item));
      },
      error: (err) => {
        console.error('Greška pri promeni statusa zadatka:', err);
        alert('Došlo je do greške pri promeni statusa zadatka.');
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
