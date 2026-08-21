import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from '../../models/user.model';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { RegisterRequest } from '../models/register-request.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/users';

  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>(
      `${this.apiUrl}/register`,
      request
    );
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      request
    );
  }

  getUser(userId: string): Observable<User> {
    return this.http.get<User>(
      `${this.apiUrl}/${userId}`
    );
  }

  updateUsername(
    userId: string,
    username: string
  ): Observable<User> {
    return this.http.put<User>(
      `${this.apiUrl}/${userId}`,
      { username }
    );
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${userId}`
    );
  }
}
