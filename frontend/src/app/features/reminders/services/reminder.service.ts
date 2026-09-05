import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Reminder } from '../models/reminder.model';

@Injectable({
  providedIn: 'root'
})
export class ReminderService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/reminders';

  getReminders(): Observable<Reminder[]> {
    return this.http.get<Reminder[]>(this.API_URL);
  }

  getDeliveredReminders(): Observable<Reminder[]> {
    return this.http.get<Reminder[]>(`${this.API_URL}/delivered`);
  }

  deleteReminder(reminderId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${reminderId}`);
  }

  deleteAllReminders(): Observable<void> {
    return this.http.delete<void>(this.API_URL);
  }
}
