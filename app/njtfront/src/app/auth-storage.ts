export interface AuthUser {
  id: number;
  ime: string;
  prezime: string;
  email: string;
  brojTelefona: string;
  uo: boolean;
  teamLeader: boolean;
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
  return !!user?.uo;
}

export function isTeamLeader(user: AuthUser | null = getCurrentUser()): boolean {
  return !!user?.teamLeader;
}
