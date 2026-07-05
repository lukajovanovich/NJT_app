import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { provideHttpClient } from '@angular/common/http'; // ✅ dodato

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    provideHttpClient() // ✅ dodato
  ],
}).catch(err => console.error(err));
