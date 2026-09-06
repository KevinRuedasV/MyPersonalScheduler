import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Note } from '../models/note.model';
import { CreateNoteRequest } from '../models/create-note-request.model';
import { DateRequest } from '../models/date-request.model';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/notes';

  getNotes(search?: string, tag?: string): Observable<Note[]> {
    let params = new HttpParams();

    if (search) {
      params = params.set('search', search);
    }

    if (tag) {
      params = params.set('tag', tag);
    }

    return this.http.get<Note[]>(this.API_URL, { params });
  }

  createNote(data: CreateNoteRequest): Observable<Note> {
    return this.http.post<Note>(this.API_URL, data);
  }

  convertToTask(noteId: string, date: string): Observable<Note> {
    const request: DateRequest = { date };

    return this.http.post<Note>(
      `${this.API_URL}/${noteId}/task`,
      request
    );
  }

  convertToEvent(noteId: string, date: string): Observable<Note> {
    const request: DateRequest = { date };

    return this.http.post<Note>(
      `${this.API_URL}/${noteId}/event`,
      request
    );
  }
}
