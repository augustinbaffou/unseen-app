import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
  picture?: string;
}

interface JwtPayload {
  sub: string;  // email ou user ID
  name?: string;
  email?: string;
  role?: string;
  picture?: string;
  id?: string;
  exp: number;
  iat: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080'; // Adaptez selon votre configuration
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUserFromToken();
  }

  /**
   * Charge l'utilisateur depuis le token JWT stocké
   */
  private loadUserFromToken(): void {
    const token = this.getToken();
    if (token) {
      try {
        const payload = this.decodeToken(token);

        // Vérifie si le token est expiré
        const now = Math.floor(Date.now() / 1000);
        if (payload.exp && payload.exp < now) {
          console.log('Token expiré');
          this.logout();
          return;
        }

        // Extrait les informations utilisateur du payload
        this.currentUserSubject.next({
          id: payload.id || payload.sub,
          name: payload.name || '',
          email: payload.email || payload.sub,
          role: payload.role || 'USER',
          picture: payload.picture
        });
      } catch (error) {
        console.error('Erreur lors du décodage du token', error);
        this.logout();
      }
    }
  }

  /**
   * Décode un JWT (sans vérification de signature côté client)
   */
  private decodeToken(token: string): JwtPayload {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      throw new Error('Token invalide');
    }
  }

  /**
   * Redirige vers l'authentification Google OAuth2
   */
  loginWithGoogle(): void {
    // Redirige vers l'endpoint OAuth2 de Spring Security
    window.location.href = `${this.API_URL}/oauth2/authorization/google`;
  }

  /**
   * Récupère le token depuis l'URL après la redirection OAuth2
   * À appeler dans un composant de callback
   */
  handleOAuthCallback(): Observable<User | null> {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (token) {
      this.setToken(token);
      this.loadUserFromToken();

      // Nettoie l'URL
      window.history.replaceState({}, document.title, window.location.pathname);
    }

    return this.currentUser$;
  }

  /**
   * Déconnexion
   */
  logout(): void {
    localStorage.removeItem('jwt_token');
    this.currentUserSubject.next(null);
  }

  /**
   * Vérifie si l'utilisateur est connecté
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    try {
      const payload = this.decodeToken(token);
      const now = Math.floor(Date.now() / 1000);
      return payload.exp ? payload.exp > now : false;
    } catch {
      return false;
    }
  }

  /**
   * Vérifie si l'utilisateur a le rôle ADMIN
   */
  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user?.role === 'ADMIN';
  }

  /**
   * Récupère le token JWT
   */
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  /**
   * Stocke le token JWT
   */
  private setToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  /**
   * Récupère l'utilisateur actuel
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}
