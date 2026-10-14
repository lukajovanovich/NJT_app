import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { getCurrentUser, hasValidSession, isAdmin, isRukovodilac } from './auth-storage';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (hasValidSession()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const user = getCurrentUser();

  if (hasValidSession() && isAdmin(user)) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const rukovodilacGuard: CanActivateFn = () => {
  const router = inject(Router);
  const user = getCurrentUser();

  if (hasValidSession() && (isRukovodilac(user) || isAdmin(user))) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
