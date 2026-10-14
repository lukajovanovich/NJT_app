import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { API_BASE_URL, APP_NAME } from '../../app/app.constants';
import { AuthUser, getCurrentUser } from '../../app/auth-storage';

interface Project {
  id: number;
  naziv: string;
}

interface Team {
  id: number;
  naziv: string;
  projekat?: {
    id: number;
  };
  clanoviTimaIds?: { id: number; ime: string; prezime: string }[];
}

interface Task {
  id: number;
  naslov: string;
  opis: string;
  status: string;
  projekatNaziv?: string | null;
  timNaziv?: string | null;
  dodeljenClanu?: AuthUser;
  datumKreiranja?: string;
}

@Component({
  selector: 'app-rukovodilac',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rukovodilac.html',
  styleUrls: ['./rukovodilac.css'],
})
export class RukovodilacPage implements OnInit {
  readonly appName = APP_NAME;

  users: AuthUser[] = [];
  projects: Project[] = [];
  teams: Team[] = [];
  createdTasks: Task[] = [];

  taskForm = {
    naslov: '',
    opis: '',
    dodeljenClanuId: '',
    projekatId: '',
    timId: '',
  };

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const currentUser = getCurrentUser();
    if (!currentUser?.id || (!currentUser.rukovodilac && !currentUser.admin)) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadUsers();
    this.loadProjects();
    this.loadTeams();
    this.loadCreatedTasks();
  }

  get availableTeams(): Team[] {
    if (!this.taskForm.projekatId) {
      return this.teams;
    }

    return this.teams.filter((team) => team.projekat?.id === Number(this.taskForm.projekatId));
  }

  get availableUsers(): AuthUser[] {
    if (!this.taskForm.timId) {
      return [];
    }

    const selectedTeam = this.teams.find((team) => team.id === Number(this.taskForm.timId));
    const memberIds = new Set((selectedTeam?.clanoviTimaIds || []).map((member) => member.id));

    return this.users.filter((user) => memberIds.has(user.id));
  }

  onProjectChange() {
    this.taskForm.timId = '';
    this.taskForm.dodeljenClanuId = '';
  }

  onTeamChange() {
    this.taskForm.dodeljenClanuId = '';
  }

  loadUsers() {
    this.http.get<AuthUser[]>(`${API_BASE_URL}/api/clan-organizacije`).subscribe({
      next: (data) => {
        this.users = data.filter((user) => !user.admin);
      },
      error: (err) => console.error('Greška pri učitavanju korisnika:', err),
    });
  }

  loadProjects() {
    this.http.get<Project[]>(`${API_BASE_URL}/api/projekat`).subscribe({
      next: (data) => {
        this.projects = data;
      },
      error: (err) => console.error('Greška pri učitavanju projekata:', err),
    });
  }

  loadTeams() {
    this.http.get<Team[]>(`${API_BASE_URL}/api/tim`).subscribe({
      next: (data) => {
        this.teams = data;
      },
      error: (err) => console.error('Greška pri učitavanju timova:', err),
    });
  }

  loadCreatedTasks() {
    this.http.get<Task[]>(`${API_BASE_URL}/api/zadatak/rukovodilac/created`).subscribe({
      next: (data) => {
        this.createdTasks = data;
      },
      error: (err) => console.error('Greška pri učitavanju kreiranih taskova:', err),
    });
  }

  createTask() {
    const payload = {
      naslov: this.taskForm.naslov,
      opis: this.taskForm.opis,
      dodeljenClanuId: Number(this.taskForm.dodeljenClanuId),
      projekatId: this.taskForm.projekatId ? Number(this.taskForm.projekatId) : null,
      timId: this.taskForm.timId ? Number(this.taskForm.timId) : null,
    };

    this.http.post(`${API_BASE_URL}/api/zadatak/rukovodilac`, payload).subscribe({
      next: () => {
        this.loadCreatedTasks();
        this.taskForm = {
          naslov: '',
          opis: '',
          dodeljenClanuId: '',
          projekatId: '',
          timId: '',
        };
      },
      error: (err) => {
        console.error('Greška pri kreiranju taska:', err);
        alert('Kreiranje taska nije uspelo.');
      },
    });
  }
}
