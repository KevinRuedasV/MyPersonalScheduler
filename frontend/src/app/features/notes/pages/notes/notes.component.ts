import { Component, computed, inject, signal } from '@angular/core';
import { NoteService } from '../../services/note.service';
import { Note, NoteType } from '../../models/note.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-notes',
  standalone: true,
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly noteService = inject(NoteService);

  readonly notes = signal<Note[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly selectedType = signal<NoteType | 'ALL'>('ALL');
  readonly searchTerm = signal('');
  readonly selectedTag = signal('');

  readonly availableTags = computed(() => {
    const tags = this.notes().flatMap(note => note.tags);
    return [...new Set(tags)].sort();
  });

  readonly filteredNotes = computed(() => {
    const type = this.selectedType();
    const search = this.searchTerm().trim().toLowerCase();
    const tag = this.selectedTag();

    return this.notes().filter(note => {
      const matchesType =
        type === 'ALL' || note.type === type;

      const matchesSearch =
        !search ||
        note.title.toLowerCase().includes(search) ||
        note.content.toLowerCase().includes(search);

      const matchesTag =
        !tag || note.tags.includes(tag);

      return matchesType && matchesSearch && matchesTag;
    });
  });

  ngOnInit(): void {
    const noteType = this.route.snapshot.data['noteType'] as NoteType | undefined;

    if (noteType) {
        this.selectedType.set(noteType);
    }
    
    this.loadNotes();
  }

  loadNotes(): void {
    this.loading.set(true);
    this.error.set('');

    this.noteService.getNotes().subscribe({
      next: (notes) => {
        this.notes.set(notes);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load your notes.');
        this.loading.set(false);
      }
    });
  }

  selectType(type: NoteType | 'ALL'): void {
    this.selectedType.set(type);
  }

  setSearchTerm(value: string): void {
    this.searchTerm.set(value);
  }

  setTag(tag: string): void {
    this.selectedTag.set(tag);
  }

  trackByNoteId(_: number, note: Note): string {
    return note.noteId;
  }
}
