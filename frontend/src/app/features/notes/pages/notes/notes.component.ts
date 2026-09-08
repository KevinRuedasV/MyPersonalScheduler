import { Component, computed, inject, signal } from '@angular/core';
import { NoteService } from '../../services/note.service';
import { Note, NoteType } from '../../models/note.model';
import { ActivatedRoute, Router } from '@angular/router';
import { NoteFormComponent } from '../../components/note-form/note-form.component';
import { NoteDetailComponent } from '../../components/note-detail/note-detail.component';

@Component({
  selector: 'app-notes',
  standalone: true,
  imports: [NoteFormComponent, NoteDetailComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
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

  readonly showNoteForm = signal(false);
  readonly formType = signal<NoteType>('NOTE');
  readonly editingNote = signal<Note | null>(null);
  readonly deletingNoteId = signal<string | null>(null);
  readonly selectedNote = signal<Note | null>(null);
  readonly showNoteDetail = signal(false);

  clearFilters(): void {
    this.searchTerm.set('');
    this.selectedTag.set('');
  }

  openNoteForm(type?: NoteType): void {
    this.editingNote.set(null);

    if (type) {
      this.formType.set(type);
    } else {
      const selectedType = this.selectedType();
      this.formType.set(selectedType === 'ALL' ? 'NOTE' : selectedType);
    }

    this.showNoteForm.set(true);
  }

  closeNoteForm(): void {
    this.showNoteForm.set(false);
    this.editingNote.set(null);
  }

  saveNote(data: {
    type: NoteType;
    title: string;
    content: string;
    tags: string[];
    date: string | null;
  }): void {
    const note = this.editingNote();

    if (note) {
      this.noteService.updateNote(note.noteId, {
        title: data.title,
        content: data.content,
        tags: data.tags,
        date: data.date
      }).subscribe({
        next: () => {
          this.closeNoteForm();
          this.router.navigate(['/notes']);
        },
        error: (error) => {
          this.error.set(
            error?.error?.message ?? 'Unable to update the note.'
          );
        }
      });

      return;
    }

    this.noteService.createNote({
      title: data.title,
      content: data.content,
      tags: data.tags
    }).subscribe({
      next: (createdNote) => {
        if (data.type === 'TASK' && data.date) {
          this.noteService.convertToTask(createdNote.noteId, data.date).subscribe({
            next: () => {
              this.closeNoteForm();
              this.loadNotes();
            },
            error: () => {
              this.error.set('Unable to create the task.');
            }
          });

          return;
        }

        if (data.type === 'EVENT' && data.date) {
          this.noteService.convertToEvent(createdNote.noteId, data.date).subscribe({
            next: () => {
              this.closeNoteForm();
              this.loadNotes();
            },
            error: () => {
              this.error.set('Unable to create the event.');
            }
          });

          return;
        }

        this.closeNoteForm();
        this.loadNotes();
      },
      error: (error) => {
        this.error.set(
          error?.error?.message ?? 'Unable to create the note.'
        );
      }
    });
  }

  openNoteDetail(note: Note): void {
    this.router.navigate(['/notes', note.noteId]);
  }

  closeNoteDetail(): void {
    this.router.navigate(['/notes']);
  }

  editFromDetail(note: Note): void {
    this.showNoteDetail.set(false);
    this.editingNote.set(note);
    this.showNoteForm.set(true);
  }

  deleteFromDetail(note: Note): void {
    this.showNoteDetail.set(false);
    this.deleteNote(note);
  }

  editNote(note: Note): void {
    this.editingNote.set(note);
    this.showNoteForm.set(true);
  }

  deleteNote(note: Note): void {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${note.title}"?`
    );

    if (!confirmed) {
      return;
    }

    this.deletingNoteId.set(note.noteId);
    this.error.set('');

    this.noteService.deleteNote(note.noteId).subscribe({
      next: () => {
        this.deletingNoteId.set(null);
        this.loadNotes();
      },
      error: (error) => {
        this.deletingNoteId.set(null);
        this.error.set(
          error?.error?.message ?? 'Unable to delete the note.'
        );
      }
    });
  }

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

        const noteId = this.route.snapshot.paramMap.get('noteId');

        if (noteId) {
          const note = notes.find(note => note.noteId === noteId);

          if (note) {
            this.selectedNote.set(note);
            this.showNoteDetail.set(true);
          }
        }
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
