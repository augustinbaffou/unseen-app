import {Component, input, output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Observable} from 'rxjs';
import {AuthService} from '../../auth/auth.service';

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
  imports: [CommonModule]
})
export class NavbarComponent {
  showMarkers = input(true);
  toggleMarkers = output<void>();

  onToggleMarkers() {
    this.toggleMarkers.emit();
  }

  currentUser$: Observable<User | null>;

  constructor(private authService: AuthService) {
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {}

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }

  logout(): void {
    this.authService.logout();
  }
}
