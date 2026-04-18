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
    path: 'bars',
    loadComponent: () => import('./pages/bars/bars').then(m => m.BarsComponent)
  },
  {
    path: 'bars/:id',
    loadComponent: () => import('./pages/bar-detail/bar-detail').then(m => m.BarDetailComponent)
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
