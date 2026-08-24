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
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  showPassword = false;
  submitting = false;
  backendError = '';

  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  get email() {
    return this.loginForm.controls.email;
  }

  get password() {
    return this.loginForm.controls.password;
  }

  submit(): void {
    this.backendError = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.submitting = true;

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/']);
      },

      error: (error) => {
        this.submitting = false;

        if (error.status === 401) {
          this.backendError = 'Invalid email or password.';
        } else if (error.status === 0) {
          this.backendError =
            'Unable to connect to the server. Please try again later.';
        } else {
          this.backendError =
            error.error?.message ||
            'Unable to sign in right now. Please try again.';
        }
      },
    });
  }
}
