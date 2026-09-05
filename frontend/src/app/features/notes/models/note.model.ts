export type NoteType = 'NOTE' | 'TASK' | 'EVENT';

export type NoteStatus =
  | 'ACTIVE'
  | 'COMPLETED'
  | 'CELEBRATED';

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
