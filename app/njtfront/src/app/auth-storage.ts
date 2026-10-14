export interface AuthUser {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  brojTelefona: string;
  admin: boolean;
  rukovodilac: boolean;
}

export interface AuthResponse {
  token: string;
  korisnik: AuthUser;
}

export function saveAuth(response: AuthResponse) {
  localStorage.setItem('authToken', response.token);
  localStorage.setItem('currentUser', JSON.stringify(response.korisnik));
  localStorage.setItem('currentUserId', response.korisnik.id.toString());
  localStorage.setItem('currentUserName', `${response.korisnik.ime} ${response.korisnik.prezime}`);
}

export function clearAuth() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('currentUser');
  localStorage.removeItem('currentUserId');
  localStorage.removeItem('currentUserName');
}

export function getToken(): string | null {
  return localStorage.getItem('authToken');
}

function isTokenExpired(token: string): boolean {
  const parts = token.split('.');
  if (parts.length !== 3) {
    return true;
  }

  try {
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
    if (typeof payload.exp !== 'number') {
      return true;
    }

    return payload.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
}

export function hasValidSession(): boolean {
  const token = getToken();
  if (!token || isTokenExpired(token)) {
    clearAuth();
    return false;
  }

  return !!getCurrentUser();
}

export function getCurrentUser(): AuthUser | null {
  const rawUser = localStorage.getItem('currentUser');
  if (!rawUser) {
    return null;
  }

  try {
    return JSON.parse(rawUser) as AuthUser;
  } catch {
    return null;
  }
}

export function isAdmin(user: AuthUser | null = getCurrentUser()): boolean {
  return !!user?.admin;
}

export function isRukovodilac(user: AuthUser | null = getCurrentUser()): boolean {
  return !!user?.rukovodilac;
}
