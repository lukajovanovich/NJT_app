import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { getCurrentUser, hasValidSession, isAdmin, isTeamLeader } from './auth-storage';

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

export const leaderGuard: CanActivateFn = () => {
  const router = inject(Router);
  const user = getCurrentUser();

  if (hasValidSession() && (isTeamLeader(user) || isAdmin(user))) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
