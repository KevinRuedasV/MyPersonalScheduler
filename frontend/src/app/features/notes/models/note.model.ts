export type NoteType = 'NOTE' | 'TASK' | 'EVENT';

export type NoteStatus =
  | 'PENDING'
  | 'UPCOMING'
  | 'COMPLETED'
  | 'CELEBRATED'
  | 'CANCELLED';

export interface Note {
  noteId: string;
  title: string;
  content: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
  type: NoteType;
  date: string | null;
  status: NoteStatus | null;
}
