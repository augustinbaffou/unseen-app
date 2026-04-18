import {Component, input, output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from '../../auth/auth.service';
import {ThemeService} from '../../services/theme.service';

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
  picture?: string;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss'],
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive]
})
export class NavbarComponent {
  showMarkers = input(true);
  toggleMarkers = output<void>();

  onToggleMarkers() {
    this.toggleMarkers.emit();
  }

  currentUser$: Observable<User | null>;

  constructor(
    private authService: AuthService,
    protected themeService: ThemeService
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }

  logout(): void {
    this.authService.logout();
  }
}
