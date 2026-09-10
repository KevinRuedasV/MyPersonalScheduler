import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../../../core/auth/services/auth.service';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../services/user.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  readonly user = signal<User | null>(null);
  readonly username = signal('');
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly deleting = signal(false);
  readonly error = signal('');
  readonly success = signal('');

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading.set(true);
    this.error.set('');
    this.success.set('');

    const userId = this.authService.getAuthenticatedUserId();

    if (!userId) {
      this.authService.logout();
      return;
    }

    this.userService.getUser(userId).subscribe({
      next: (user) => {
        this.user.set(user);
        this.username.set(user.username);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.error.set(
          error?.error?.message ?? 'Unable to load your profile.'
        );
      }
    });
  }

  saveProfile(): void {
    const currentUser = this.user();
    const trimmedUsername = this.username().trim();

    if (!currentUser) return;

    if (!trimmedUsername) {
      this.error.set('Username cannot be empty.');
      this.success.set('');
      return;
    }

    this.saving.set(true);
    this.error.set('');
    this.success.set('');

    this.userService.updateUsername(currentUser.userId, {
      username: trimmedUsername
    }).subscribe({
      next: (updatedUser) => {
        this.user.set(updatedUser);
        this.username.set(updatedUser.username);
        this.saving.set(false);
        this.success.set('Profile updated successfully.');
      },
      error: (error) => {
        this.saving.set(false);
        this.error.set(
          error?.error?.message ?? 'Unable to update your profile.'
        );
      }
    });
  }

  deleteAccount(): void {
    const currentUser = this.user();

    if (!currentUser) return;

    const confirmed = window.confirm(
      'Are you sure you want to delete your account? This action cannot be undone.'
    );

    if (!confirmed) return;

    this.deleting.set(true);
    this.error.set('');

    this.userService.deleteUser(currentUser.userId).subscribe({
      next: () => {
        this.deleting.set(false);
        this.authService.logout();
      },
      error: (error) => {
        this.deleting.set(false);
        this.error.set(
          error?.error?.message ?? 'Unable to delete your account.'
        );
      }
    });
  }

  setUsername(value: string): void {
    this.username.set(value);
  }

}
