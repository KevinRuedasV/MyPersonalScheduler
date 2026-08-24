import { Routes } from '@angular/router';

import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./core/auth/pages/login/login.component')
            .then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./core/auth/pages/register/register.component')
            .then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: '',
    canActivate: [authGuard],
    children: []
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];
