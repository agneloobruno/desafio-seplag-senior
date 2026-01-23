export interface Artist {
  id: number;
  nome: string;
  fotoUrl?: string | null;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // página atual (0-based do Spring Boot)
}
