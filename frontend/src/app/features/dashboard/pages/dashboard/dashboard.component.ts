import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Note, NoteStatus } from '../../../notes/models/note.model';
import { NoteService } from '../../../notes/services/note.service';
import { Reminder } from '../../../reminders/models/reminder.model';
import { ReminderService } from '../../../reminders/services/reminder.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  private readonly noteService = inject(NoteService);
  private readonly reminderService = inject(ReminderService);

  readonly notes = signal<Note[]>([]);
  readonly reminders = signal<Reminder[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly totalNotes = computed(() => this.notes().length);

  readonly pendingTasks = computed(() =>
    this.notes().filter(
      note => note.type === 'TASK' && note.status === 'PENDING'
    ).length
  );

  readonly upcomingEvents = computed(() =>
    this.notes().filter(
      note =>
        note.type === 'EVENT' &&
        note.date &&
        !this.isFinished(note)
    ).length
  );

  readonly upcomingTasks = computed(() =>
    this.notes()
      .filter(
        note =>
          note.type === 'TASK' &&
          note.date &&
          !this.isFinished(note) &&
          this.isTodayOrFuture(note.date)
      )
      .sort((a, b) => this.compareDates(a.date, b.date))
      .slice(0, 5)
  );

  readonly upcomingEventList = computed(() =>
    this.notes()
      .filter(
        note =>
          note.type === 'EVENT' &&
          note.date &&
          !this.isFinished(note) &&
          this.isTodayOrFuture(note.date)
      )
      .sort((a, b) => this.compareDates(a.date, b.date))
      .slice(0, 5)
  );

  readonly recentNotes = computed(() =>
    [...this.notes()]
      .sort(
        (a, b) =>
          new Date(b.updatedAt).getTime() -
          new Date(a.updatedAt).getTime()
      )
      .slice(0, 5)
  );

  readonly recentReminders = computed(() =>
    [...this.reminders()]
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
      )
      .slice(0, 5)
  );

  constructor() {
    this.loadDashboard();
  }

    loadDashboard(): void {
    this.loading.set(true);
    this.error.set('');

    forkJoin({
        notes: this.noteService.getNotes(),
        reminders: this.reminderService.getReminders()
    }).subscribe({
        next: ({ notes, reminders }) => {
        this.notes.set(notes);
        this.reminders.set(reminders);
        this.loading.set(false);
        },
        error: () => {
        this.error.set('Unable to load your dashboard.');
        this.loading.set(false);
        }
    });
    }

  private isFinished(note: Note): boolean {
    const finishedStatuses: NoteStatus[] = [
      'COMPLETED',
      'CELEBRATED',
      'CANCELLED'
    ];

    return note.status !== null &&
      finishedStatuses.includes(note.status);
  }

  private isTodayOrFuture(date: string): boolean {
    const today = new Date();
    const target = new Date(`${date}T00:00:00`);

    today.setHours(0, 0, 0, 0);

    return target >= today;
  }

  private compareDates(
    first: string | null,
    second: string | null
  ): number {
    if (!first || !second) {
      return 0;
    }

    return (
      new Date(`${first}T00:00:00`).getTime() -
      new Date(`${second}T00:00:00`).getTime()
    );
  }

  trackByNoteId(_: number, note: Note): string {
    return note.noteId;
  }

  trackByReminderId(_: number, reminder: Reminder): string {
    return reminder.reminderId;
  }
}
