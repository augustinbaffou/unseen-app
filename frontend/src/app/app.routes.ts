import { Routes } from '@angular/router';
import { MapComponent } from './pages/map/map';
import {OAuthCallbackComponent} from './auth/callback';
import {AuthGuard} from './auth/auth.guard';

export const routes: Routes = [
  {
    path: 'oauth/callback',
    component: OAuthCallbackComponent
  },
  {
    path: '',
    component: MapComponent
  },
  {
    path: 'login',
    loadComponent: () => import('./auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
