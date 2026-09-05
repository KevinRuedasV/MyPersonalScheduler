import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Note } from '../models/note.model';

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
}
