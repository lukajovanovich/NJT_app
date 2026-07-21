import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { clearAuth, getToken } from './auth-storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = getToken();
  const router = inject(Router);

  const request = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    : req;

  return next(request).pipe(
    catchError((error) => {
      if (error.status === 401 && !req.url.includes('/api/clan-organizacije/login')) {
        clearAuth();
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
