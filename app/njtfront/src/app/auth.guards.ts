import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { getCurrentUser, getToken, isAdmin, isTeamLeader } from './auth-storage';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (getToken() && getCurrentUser()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const user = getCurrentUser();

  if (getToken() && isAdmin(user)) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const leaderGuard: CanActivateFn = () => {
  const router = inject(Router);
  const user = getCurrentUser();

  if (getToken() && (isTeamLeader(user) || isAdmin(user))) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
