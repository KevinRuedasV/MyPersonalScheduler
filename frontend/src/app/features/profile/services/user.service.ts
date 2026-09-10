import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from '../../../core/models/user.model';
import { UpdateUserRequest } from '../models/update-user-request.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/users';

  getUser(userId: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${userId}`);
  }

  updateUsername(
    userId: string,
    data: UpdateUserRequest
  ): Observable<User> {
    return this.http.put<User>(
      `${this.API_URL}/${userId}`,
      data
    );
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${userId}`
    );
  }
}
