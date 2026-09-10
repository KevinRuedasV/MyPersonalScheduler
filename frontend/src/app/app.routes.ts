import { Routes } from '@angular/router';

import { authGuard } from './core/auth/guards/auth.guard';
import { AppShellComponent } from './shell/app-shell.component';
import { guestGuard } from './core/auth/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        canActivate: [guestGuard],
        loadComponent: () =>
          import('./core/auth/pages/login/login.component')
            .then(m => m.LoginComponent)
      },
      {
        path: 'register',
        canActivate: [guestGuard],
        loadComponent: () =>
          import('./core/auth/pages/register/register.component')
            .then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: '',
    component: AppShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },
      {
        path: 'notes',
        loadComponent: () =>
          import('./features/notes/pages/notes/notes.component')
            .then(m => m.NotesComponent)
      },
      {
        path: 'notes/:noteId',
        loadComponent: () =>
          import('./features/notes/pages/notes/notes.component')
            .then(m => m.NotesComponent)
      },
      {
        path: 'tasks',
        loadComponent: () =>
          import('./features/notes/pages/notes/notes.component')
            .then(m => m.NotesComponent),
        data: {
          noteType: 'TASK'
        }
      },
      {
        path: 'events',
        loadComponent: () =>
          import('./features/notes/pages/notes/notes.component')
            .then(m => m.NotesComponent),
        data: {
          noteType: 'EVENT'
        }
      },
      {
        path: 'calendar',
        loadComponent: () =>
          import('./features/calendar/pages/calendar/calendar.component')
            .then(m => m.CalendarComponent)
      },
      {
        path: 'reminders',
        loadComponent: () =>
          import('./features/reminders/pages/reminders/reminders.component')
            .then(m => m.RemindersComponent)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/pages/profile/profile.component')
            .then(m => m.ProfileComponent)
      },
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];
