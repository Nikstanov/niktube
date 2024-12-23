import type { Author } from "./Author";

export type Video = {
  videoId?: string; // UUID can be represented as a string in TypeScript
  title: string;
  description: string;
  owner: Author;
  thumbnail: string;
  open: boolean;
  created: Date;
  views: number;
};
