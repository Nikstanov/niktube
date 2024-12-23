import type { Stream } from '@/entities/Stream';
import { defineStore } from 'pinia';
import axiosInstance from '../uttils/axiosInstance';

export const useStreamStore = defineStore('streamStore', {
  state: () => ({
    streams: [] as Stream[],
    loading: false,
    hasMore: true,
    page: 0,
  }),
  actions: {
    async fetchUserStreams(userId: string) {
      if (this.loading) return;

      this.loading = true;
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/streams/public/${userId}`
        );
        const data = await response.data;
        const newStreams = data as Stream[];

        this.streams.push(...newStreams);

      } catch (error) {
        console.error('Error fetching streams:', error);
      } finally {
        this.loading = false;
      }
    },
    async fetchStreams(pageSize = 20) {
      if (this.loading) return;

      this.loading = true;
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/streams/public?page=${this.page}&size=${pageSize}`
        );
        const data = await response.data;
        const newStreams = data as Stream[];

        this.streams.push(...newStreams);

        // Determine if there are more streams to load
        this.hasMore = newStreams.length === pageSize;
        this.page += 1;
      } catch (error) {
        console.error('Error fetching streams:', error);
      } finally {
        this.loading = false;
      }
    },
    async getStreamById(streamId: string) {
      // Check if the stream already exists in the store
      const existingStream = this.streams.find((stream) => stream.id === streamId);
      if (existingStream) {
        return existingStream;
      }

      // Fetch the stream from the server if not in the store
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/streams/${streamId}`
        );
        const stream = await response.data;
        this.streams.push(stream);
        return stream;
      } catch (error) {
        console.error('Error fetching stream by ID:', error);
        return null;
      }
    },
  },
});
