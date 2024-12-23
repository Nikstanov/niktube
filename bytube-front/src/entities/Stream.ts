import type { Author } from "./Author";

export type Stream = {
  id?: string; // UUID can be represented as a string in TypeScript
  name: string;
  owner: Author;
  startTime: string;
};
