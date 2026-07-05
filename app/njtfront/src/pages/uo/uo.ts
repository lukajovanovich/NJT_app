import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface Member {
  ime: string;
  prezime: string;
  email: string;
  brojTelefona: string;
}

interface PrijavaItem {
  Ime: string;
  Prezime: string;
  Projekat: string;
  Tim: string;
  clanId: number;
  status: string;
  teamId?: number;
}

interface Project {
  Naziv: string;
  DatumPocetka: string;
  DatumZavrsetka: string;
  Koordinator: string;
}

@Component({
  selector: 'app-uo',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './uo.html',
  styleUrls: ['./uo.css'],
})
export class UoPage implements OnInit {
  activeProjects: Project[] = [];
  prijave: PrijavaItem[] = [];

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    if (!currentUser?.id || !currentUser?.uo) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadActiveProjects();
    this.loadPrijave();
  }

  loadActiveProjects() {
    this.http.get<any[]>('http://localhost:8080/api/projekat').subscribe({
      next: (data) => {
        this.activeProjects = data.map((project) => ({
          Naziv: project.naziv,
          DatumPocetka: this.formatDate(project.datumPocetka),
          DatumZavrsetka: this.formatDate(project.datumZavrsetka),
          Koordinator: `${project.odabraniKoordinator.ime} ${project.odabraniKoordinator.prezime}`,
        }));
      },
      error: (err) => console.error('Greska pri ucitavanju aktivnih projekata (UO):', err),
    });
  }

  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('sr-RS');
  }

  loadPrijave() {
    const prijaveLS: any[] = JSON.parse(localStorage.getItem('mojiProjekti') || '[]');

    this.prijave = prijaveLS
      .filter((project) => this.normalizeStatus(project.status) === 'prijavljen')
      .map((project: any) => {
        const clan: Member = project.clanoviTima?.[0] || {
          ime: 'Nepoznato',
          prezime: '',
          email: '',
          brojTelefona: '',
        };

        return {
          Ime: clan.ime,
          Prezime: clan.prezime,
          Projekat: project.naziv || 'Nepoznat projekat',
          Tim: project.tim || 'Nepoznat tim',
          clanId: project.clanId,
          status: project.status || 'Prijavljen',
          teamId: project.teamId,
        };
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
    const svePrijave: any[] = JSON.parse(localStorage.getItem('mojiProjekti') || '[]');

    const index = svePrijave.findIndex(
      (project) =>
        project.clanId === prijava.clanId && project.naziv === prijava.Projekat && project.tim === prijava.Tim
    );

    if (index !== -1) {
      svePrijave[index].status = noviStatus;
    }
    localStorage.setItem('mojiProjekti', JSON.stringify(svePrijave));

    if (noviStatus === 'Prihvacen' && prijava.teamId) {
      this.http
        .post('http://localhost:8080/api/clanTim/povezi', null, {
          params: {
            clanId: prijava.clanId.toString(),
            timId: prijava.teamId.toString(),
          },
        })
        .subscribe({
          next: (res) => console.log('Podaci ubaceni u bazu:', res),
          error: (err) => console.error('Greska pri ubacivanju u bazu:', err),
        });
    }

    this.prijave = this.prijave.filter(
      (project) =>
        !(project.clanId === prijava.clanId && project.Projekat === prijava.Projekat && project.Tim === prijava.Tim)
    );

    console.log(`Prijava za ${prijava.Projekat} azurirana kao ${noviStatus} i uklonjena sa prikaza.`);
  }
}
