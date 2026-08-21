import { Routes } from '@angular/router';

import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
    ]
  },
  {
    path: '',
    canActivate: [authGuard],
    children: [
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];