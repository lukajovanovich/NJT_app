import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { API_BASE_URL, APP_NAME } from '../../app/app.constants';
import { getCurrentUser } from '../../app/auth-storage';

interface Member {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  brojTelefona: string;
  admin: boolean;
  rukovodilac: boolean;
}

interface PrijavaItem {
  id: number;
  Ime: string;
  Prezime: string;
  Projekat: string;
  Tim: string;
  clanId: number;
  status: string;
  teamId?: number;
}

interface BackendPrijavaItem {
  id: number;
  clanId: number;
  timId: number;
  naziv: string;
  tim: string;
  status: string;
  clan?: Member;
}

interface Project {
  id: number;
  naziv: string;
  datumPocetka: string;
  datumZavrsetka: string;
  odabraniKoordinator: Member | null;
}

interface ProjectListItem extends Project {
  datumPocetkaPrikaz: string;
  datumZavrsetkaPrikaz: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrls: ['./admin.css'],
})
export class AdminPage implements OnInit, OnDestroy {
  readonly appName = APP_NAME;

  @ViewChild('projectFormRef') projectFormRef?: NgForm;

  activeProjects: ProjectListItem[] = [];
  prijave: PrijavaItem[] = [];
  coordinators: Member[] = [];
  editingProjectId: number | null = null;
  prijaveLoadError = '';
  private prijaveRefreshId: ReturnType<typeof window.setInterval> | null = null;

  projectForm = {
    naziv: '',
    datumPocetka: '',
    datumZavrsetka: '',
    odabraniKoordinatorId: '',
  };

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const currentUser = getCurrentUser();
    if (!currentUser?.id || !currentUser.admin) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadActiveProjects();
    this.loadPrijave();
    this.loadCoordinators();
    this.prijaveRefreshId = window.setInterval(() => this.loadPrijave(), 15000);
  }

  ngOnDestroy(): void {
    if (this.prijaveRefreshId !== null) {
      window.clearInterval(this.prijaveRefreshId);
      this.prijaveRefreshId = null;
    }
  }

  loadActiveProjects() {
    this.http.get<Project[]>(`${API_BASE_URL}/api/projekat`).subscribe({
      next: (data) => {
        this.activeProjects = data.map((project) => ({
          ...project,
          datumPocetkaPrikaz: this.formatDate(project.datumPocetka),
          datumZavrsetkaPrikaz: this.formatDate(project.datumZavrsetka),
        }));
      },
      error: (err) => console.error('Greska pri ucitavanju projekata:', err),
    });
  }

  loadCoordinators() {
    this.http.get<Member[]>(`${API_BASE_URL}/api/clan-organizacije`).subscribe({
      next: (data) => {
        this.coordinators = data.filter((user) => user.rukovodilac || user.admin);
      },
      error: (err) => console.error('Greska pri ucitavanju koordinatora:', err),
    });
  }

  loadPrijave() {
    this.http.get<BackendPrijavaItem[]>(`${API_BASE_URL}/api/prijava/pending`).subscribe({
      next: (data) => {
        this.prijaveLoadError = '';
        this.prijave = data.map((prijava) => ({
          id: prijava.id,
          Ime: prijava.clan?.ime || 'Nepoznato',
          Prezime: prijava.clan?.prezime || '',
          Projekat: prijava.naziv || 'Nepoznat projekat',
          Tim: prijava.tim || 'Nepoznat tim',
          clanId: prijava.clanId,
          status: prijava.status || 'Prijavljen',
          teamId: prijava.timId,
        }));
      },
      error: (err) => {
        console.error('Greska pri ucitavanju prijava:', err);
        this.prijave = [];
        this.prijaveLoadError = 'Prijave trenutno nisu mogle da se ucitaju. Osvezite stranicu ili se prijavite ponovo.';
      },
    });
  }

  prihvati(prijava: PrijavaItem) {
    this.updateStatus(prijava, 'Prihvacen');
  }

  odbij(prijava: PrijavaItem) {
    this.updateStatus(prijava, 'Odbijen');
  }

  isAccepted(status: string): boolean {
    return this.normalizeStatus(status) === 'prihvacen';
  }

  isRejected(status: string): boolean {
    return this.normalizeStatus(status) === 'odbijen';
  }

  private normalizeStatus(status: string): string {
    return status
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase();
  }

  updateStatus(prijava: PrijavaItem, noviStatus: string) {
    this.http.put(`${API_BASE_URL}/api/prijava/${prijava.id}/status`, { status: noviStatus }).subscribe({
      next: () => {
        this.prijave = this.prijave.filter((project) => project.id !== prijava.id);
      },
      error: (err) => {
        console.error('Greska pri azuriranju statusa prijave:', err);
        alert('Doslo je do greske pri promeni statusa prijave.');
      },
    });
  }

  startEdit(project: ProjectListItem) {
    this.editingProjectId = project.id;
    this.projectForm = {
      naziv: project.naziv,
      datumPocetka: this.toInputDate(project.datumPocetka),
      datumZavrsetka: this.toInputDate(project.datumZavrsetka),
      odabraniKoordinatorId: project.odabraniKoordinator?.id?.toString() || '',
    };
  }

  resetProjectForm() {
    this.editingProjectId = null;
    this.projectFormRef?.resetForm();
    this.projectForm = {
      naziv: '',
      datumPocetka: '',
      datumZavrsetka: '',
      odabraniKoordinatorId: '',
    };
  }

  saveProject() {
    this.projectFormRef?.form.markAllAsTouched();

    const coordinator = this.coordinators.find((item) => item.id === Number(this.projectForm.odabraniKoordinatorId)) || null;
    const payload = {
      id: this.editingProjectId ?? 0,
      naziv: this.projectForm.naziv,
      datumPocetka: this.projectForm.datumPocetka,
      datumZavrsetka: this.projectForm.datumZavrsetka,
      odabraniKoordinator: coordinator,
    };

    const request$ = this.editingProjectId
      ? this.http.put<Project>(`${API_BASE_URL}/api/projekat`, payload)
      : this.http.post<Project>(`${API_BASE_URL}/api/projekat`, payload);

    request$.subscribe({
      next: () => {
        this.loadActiveProjects();
        this.resetProjectForm();
      },
      error: (err) => {
        console.error('Greska pri cuvanju projekta:', err);
        alert('Došlo je do greške pri čuvanju projekta.');
      },
    });
  }

  deleteProject(project: ProjectListItem) {
    if (!confirm(`Da li sigurno zelis da obrises projekat "${project.naziv}"?`)) {
      return;
    }

    this.http.delete(`${API_BASE_URL}/api/projekat/${project.id}`).subscribe({
      next: () => {
        this.activeProjects = this.activeProjects.filter((item) => item.id !== project.id);
        if (this.editingProjectId === project.id) {
          this.resetProjectForm();
        }
      },
      error: (err) => {
        console.error('Greska pri brisanju projekta:', err);
        alert('Brisanje projekta nije uspelo.');
      },
    });
  }

  private formatDate(dateString: string): string {
    if (!dateString) {
      return '';
    }

    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) {
      return dateString;
    }

    return date.toLocaleDateString('sr-RS');
  }

  private toInputDate(dateString: string): string {
    if (!dateString) {
      return '';
    }

    const isoDateMatch = dateString.match(/^\d{4}-\d{2}-\d{2}/);
    if (isoDateMatch) {
      return isoDateMatch[0];
    }

    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    return date.toISOString().slice(0, 10);
  }
}
