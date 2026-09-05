import { Component, computed, inject, signal } from '@angular/core';

import { ReminderService } from '../../services/reminder.service';
import { Reminder } from '../../models/reminder.model';

@Component({
  selector: 'app-reminders',
  standalone: true,
  templateUrl: './reminders.component.html',
  styleUrl: './reminders.component.css'
})
export class RemindersComponent {
  private readonly reminderService = inject(ReminderService);

  readonly reminders = signal<Reminder[]>([]);
  readonly deliveredReminders = signal<Reminder[]>([]);

  readonly loading = signal(true);
  readonly error = signal('');

  readonly activeReminders = computed(() =>
    this.reminders().filter(reminder =>
      !this.deliveredReminders().some(
        delivered => delivered.reminderId === reminder.reminderId
      )
    )
  );

  ngOnInit(): void {
    this.loadReminders();
  }

  loadReminders(): void {
    this.loading.set(true);
    this.error.set('');

    this.reminderService.getReminders().subscribe({
      next: (reminders) => {
        this.reminders.set(reminders);
        this.loadDeliveredReminders();
      },
      error: () => {
        this.error.set('Unable to load your reminders.');
        this.loading.set(false);
      }
    });
  }

  private loadDeliveredReminders(): void {
    this.reminderService.getDeliveredReminders().subscribe({
      next: (reminders) => {
        this.deliveredReminders.set(reminders);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load delivered reminders.');
        this.loading.set(false);
      }
    });
  }

  deleteReminder(reminderId: string): void {
    this.reminderService.deleteReminder(reminderId).subscribe({
      next: () => {
        this.reminders.update(reminders =>
          reminders.filter(reminder => reminder.reminderId !== reminderId)
        );

        this.deliveredReminders.update(reminders =>
          reminders.filter(reminder => reminder.reminderId !== reminderId)
        );
      },
      error: () => {
        this.error.set('Unable to delete the reminder.');
      }
    });
  }

  deleteAllReminders(): void {
    this.reminderService.deleteAllReminders().subscribe({
      next: () => {
        this.reminders.set([]);
        this.deliveredReminders.set([]);
      },
      error: () => {
        this.error.set('Unable to delete the reminders.');
      }
    });
  }

  formatDateTime(value: string): string {
    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString('en-GB', {
      dateStyle: 'medium',
      timeStyle: 'short'
    });
  }
}
