export interface UpdateNoteRequest {
  title: string;
  content: string;
  tags: string[];
  date: string | null;
}
