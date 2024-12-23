// stores/videoStore.ts
import type { Video } from '@/entities/Video';
import { defineStore } from 'pinia';
import axiosInstance from '../uttils/axiosInstance';

export const useVideoStore = defineStore('videoStore', {
  state: () => ({
    videos: [] as Video[],
    loading: false,
    hasMore: true,
    page: 0,
  }),
  actions: {
    async resetVideos(){
      this.videos = []
      this.page = 0
    },
    async findVideos(str: string){
      if (this.loading) return;

      this.loading = true;
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/video/public/search?name=${str}`
        );
        const data = await response.data;
        const newVideos = data as Video[];

        this.videos.push(...newVideos);

      } catch (error) {
        console.error('Error fetching videos:', error);
      } finally {
        this.loading = false;
      }
    },
    async fetchUserVideos(userId: string, pageSize = 20) {
      if (this.loading) return;

      this.loading = true;
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/video/user/${userId}?page=${this.page}&size=${pageSize}`
        );
        const data = await response.data;
        const newVideos = data as Video[];

        this.videos.push(...newVideos);

        this.hasMore = newVideos.length === pageSize;
        this.page += 1;
      } catch (error) {
        console.error('Error fetching videos:', error);
      } finally {
        this.loading = false;
      }
    },
    async fetchVideos(pageSize = 20) {
      if (this.loading) return;

      this.loading = true;
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/video/public?page=${this.page}&size=${pageSize}`
        );
        const data = await response.data;
        const newVideos = data as Video[];

        this.videos.push(...newVideos);

        this.hasMore = newVideos.length === pageSize;
        this.page += 1;
      } catch (error) {
        console.error('Error fetching videos:', error);
      } finally {
        this.loading = false;
      }
    },
    async getVideoById(videoId: string) {
      // Check if the video already exists in the store
      const existingVideo = this.videos.find((video) => video.videoId === videoId);
      if (existingVideo) {
        return existingVideo;
      }

      // If not, fetch the video from the server
      try {
        const response = await axiosInstance.get(
          `http://localhost:8080/video/public/${videoId}`
        );
        const video = await response.data;
        // Add the video to the store if fetched successfully
        this.videos.push(video);
        return video;
      } catch (error) {
        console.error('Error fetching video by ID:', error);
        return null;
      }
    },

    async deleteVideo(videoId: string){
      try {
        await axiosInstance.delete(
          `http://localhost:8080/video/${videoId}`
        );
      } catch (error) {
        console.error('Error deleting video by ID:', error);
        return null;
      }
    },

    async updateStatus(videoId: string){
      try {
        await axiosInstance.patch(
          `http://localhost:8080/video/${videoId}`
        );
      } catch (error) {
        console.error('Error updating video by ID:', error);
        return null;
      }
    }
  },
});
