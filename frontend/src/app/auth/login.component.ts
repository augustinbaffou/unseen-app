import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from './auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-cream">
      <div class="w-full max-w-[400px] bg-surface rounded-card px-8 py-10 flex flex-col items-center shadow-[var(--shadow-standard)]">

        <div class="text-center mb-2">
          <h1 class="font-display text-[2rem] font-bold text-charcoal tracking-tight m-0">Unseen</h1>
          <p class="font-body text-[0.9375rem] text-smoke italic mt-2 mb-0">Redécouvrez votre ville, un verre à la fois.</p>
        </div>

        <p class="text-sm text-muted mt-6 mb-7">Connectez-vous pour continuer</p>

        <button
          (click)="loginWithGoogle()"
          class="w-full flex items-center justify-center gap-3 bg-transparent border-2 border-terracotta rounded-btn px-5 py-3 font-body font-semibold text-charcoal cursor-pointer hover:bg-terracotta/10 hover:shadow-[var(--shadow-subtle)] active:scale-[0.98]"
        >
          <svg class="w-5 h-5 shrink-0" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
          </svg>
          Se connecter avec Google
        </button>

        <p class="text-xs text-muted text-center mt-6">
          En vous connectant, vous acceptez nos conditions d'utilisation.
        </p>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent {
  constructor(private authService: AuthService) {}

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }
}
