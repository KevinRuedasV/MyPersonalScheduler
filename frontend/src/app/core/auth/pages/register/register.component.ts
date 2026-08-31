import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  showPassword = false;
  submitting = false;
  backendError = '';

  readonly registerForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    username: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(24),
        Validators.pattern(/^[a-zA-Z0-9_]+$/),
      ],
    ],
    password: ['', [Validators.required]],
  });

  get email() {
    return this.registerForm.controls.email;
  }

  get username() {
    return this.registerForm.controls.username;
  }

  get password() {
    return this.registerForm.controls.password;
  }

  submit(): void {
    this.backendError = '';

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.submitting = true;

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/auth/login']);
      },

      error: (error) => {
        this.submitting = false;

        if (error.status === 409) {
          this.backendError =
            'That email or username is already registered.';
        } else if (error.status === 0) {
          this.backendError =
            'Unable to connect to the server. Please try again later.';
        } else {
          this.backendError =
            error.error?.message ||
            'Unable to create your account right now.';
        }
      },
    });
  }
}
