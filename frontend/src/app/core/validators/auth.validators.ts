import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

export function emailValidator(
  control: AbstractControl
): ValidationErrors | null {
  const value = String(control.value ?? '').trim();

  if (!value) {
    return { required: true };
  }

  const valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  return valid ? null : { email: true };
}

export function usernameValidator(
  control: AbstractControl
): ValidationErrors | null {
  const value = String(control.value ?? '').trim();

  if (!value) {
    return { required: true };
  }

  if (value.length < 3) {
    return { minlength: true };
  }

  if (value.length > 24) {
    return { maxlength: true };
  }

  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    return { pattern: true };
  }

  return null;
}

export function passwordValidator(
  control: AbstractControl
): ValidationErrors | null {
  const value = String(control.value ?? '');

  if (!value) {
    return { required: true };
  }

  if (value.length < 8) {
    return { minlength: true };
  }

  return null;
}
