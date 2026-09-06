import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NoteType } from '../../models/note.model';

@Component({
  selector: 'app-note-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './note-form.component.html',
  styleUrl: './note-form.component.css'
})
export class NoteFormComponent {
  @Input() initialType: NoteType = 'NOTE';

  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<{
    type: NoteType;
    title: string;
    content: string;
    tags: string[];
    date: string | null;
  }>();

  readonly type = signal<NoteType>('NOTE');
  readonly title = signal('');
  readonly content = signal('');
  readonly tags = signal<string[]>([]);
  readonly tagInput = signal('');
  readonly date = signal('');

  readonly error = signal('');

  ngOnInit(): void {
    this.type.set(this.initialType);
  }

  setType(value: string): void {
    this.type.set(value as NoteType);

    if (value === 'NOTE') {
      this.date.set('');
    }
  }

  setTitle(value: string): void {
    this.title.set(value);
  }

  setContent(value: string): void {
    this.content.set(value);
  }

  setTagInput(value: string): void {
    this.tagInput.set(value);
  }

  setDate(value: string): void {
    this.date.set(value);
  }

  addTag(): void {
    const tag = this.tagInput().trim();

    if (!tag || this.tags().includes(tag)) {
      return;
    }

    this.tags.update(tags => [...tags, tag]);
    this.tagInput.set('');
  }

  removeTag(tag: string): void {
    this.tags.update(tags => tags.filter(value => value !== tag));
  }

  handleTagKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.addTag();
    }
  }

    submit(): void {
    this.error.set('');

    if (!this.title().trim()) {
        this.error.set('Title is required.');
        return;
    }

    if (this.type() !== 'NOTE' && !this.date()) {
        this.error.set('Date is required.');
        return;
    }

    this.submitted.emit({
        type: this.type(),
        title: this.title().trim(),
        content: this.content(),
        tags: this.tags(),
        date: this.type() === 'NOTE' ? null : this.date()
    });
    }

  close(): void {
    this.closed.emit();
  }
}
