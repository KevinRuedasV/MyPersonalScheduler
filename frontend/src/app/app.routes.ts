import { Routes } from '@angular/router';

import { authGuard } from './core/auth/guards/auth.guard';
import { AppShellComponent } from './shell/app-shell.component';

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
    component: AppShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'notes',
        pathMatch: 'full'
      },
      {
        path: 'notes',
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
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];
