import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-oauth-callback',
  standalone: true,
  template: `
    <div class="flex items-center justify-center min-h-screen bg-gray-100">
      <div class="text-center">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        <p class="mt-4 text-gray-600 font-medium">Connexion en cours...</p>
      </div>
    </div>
  `,
  styles: []
})
export class OAuthCallbackComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.handleOAuthCallback().subscribe({
      next: (user) => {
        if (user) {
          // Redirige vers la page d'accueil après connexion réussie
          this.router.navigate(['/']);
        } else {
          // Si pas d'utilisateur, redirige vers login
          this.router.navigate(['/login']);
        }
      },
      error: (error) => {
        console.error('Erreur lors de la connexion', error);
        this.router.navigate(['/login']);
      }
    });
  }
}
