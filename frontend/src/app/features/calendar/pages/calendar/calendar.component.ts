import { Component, computed, inject, signal } from '@angular/core';
import { NoteService } from '../../../notes/services/note.service';
import { Note } from '../../../notes/models/note.model';

interface CalendarDay {
  date: Date;
  dateKey: string;
  isCurrentMonth: boolean;
  isToday: boolean;
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css'
})
export class CalendarComponent {
  private readonly noteService = inject(NoteService);

  readonly notes = signal<Note[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly currentDate = signal(new Date());

  readonly monthLabel = computed(() =>
    this.currentDate().toLocaleDateString('en-US', {
      month: 'long',
      year: 'numeric'
    })
  );

  readonly calendarDays = computed(() => {
    const current = this.currentDate();

    const year = current.getFullYear();
    const month = current.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    const startDay = firstDay.getDay();
    const daysInMonth = lastDay.getDate();

    const previousMonthDays = startDay === 0 ? 6 : startDay - 1;

    const days: CalendarDay[] = [];

    for (let i = previousMonthDays; i > 0; i--) {
      const date = new Date(year, month, 1 - i);

      days.push(this.createCalendarDay(date, false));
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(year, month, day);

      days.push(this.createCalendarDay(date, true));
    }

    const remainingDays = (7 - (days.length % 7)) % 7;

    for (let i = 1; i <= remainingDays; i++) {
      const date = new Date(year, month + 1, i);

      days.push(this.createCalendarDay(date, false));
    }

    return days;
  });

  ngOnInit(): void {
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
        this.error.set('Unable to load your calendar.');
        this.loading.set(false);
      }
    });
  }

  previousMonth(): void {
    const current = this.currentDate();

    this.currentDate.set(
      new Date(current.getFullYear(), current.getMonth() - 1, 1)
    );
  }

  nextMonth(): void {
    const current = this.currentDate();

    this.currentDate.set(
      new Date(current.getFullYear(), current.getMonth() + 1, 1)
    );
  }

  goToToday(): void {
    this.currentDate.set(new Date());
  }

  getNotesForDay(date: Date): Note[] {
    const dateKey = this.toDateKey(date);

    return this.notes().filter(note =>
      note.date === dateKey &&
      (note.type === 'TASK' || note.type === 'EVENT')
    );
  }

  private createCalendarDay(
    date: Date,
    isCurrentMonth: boolean
  ): CalendarDay {
    const today = new Date();

    return {
      date,
      dateKey: this.toDateKey(date),
      isCurrentMonth,
      isToday:
        date.getFullYear() === today.getFullYear() &&
        date.getMonth() === today.getMonth() &&
        date.getDate() === today.getDate()
    };
  }

  private toDateKey(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
