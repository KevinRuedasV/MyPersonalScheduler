import { Component, input, output } from '@angular/core';

import { Note } from '../../models/note.model';

@Component({
  selector: 'app-note-detail',
  standalone: true,
  templateUrl: './note-detail.component.html',
  styleUrl: './note-detail.component.css'
})
export class NoteDetailComponent {
  readonly note = input.required<Note>();

  readonly closed = output<void>();
  readonly editRequested = output<Note>();
  readonly deleteRequested = output<Note>();

  close(): void {
    this.closed.emit();
  }

  edit(): void {
    this.editRequested.emit(this.note());
  }

  delete(): void {
    this.deleteRequested.emit(this.note());
  }
}
