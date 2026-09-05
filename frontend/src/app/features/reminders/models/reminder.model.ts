export type ReminderStatus = string;

export interface Reminder {
  reminderId: string;
  noteId: string;
  message: string;
  reminderDateTime: string;
  status: ReminderStatus;
  createdAt: string;
}
