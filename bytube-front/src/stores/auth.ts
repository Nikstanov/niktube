// src/stores/auth.ts
import { defineStore } from 'pinia';
import axiosInstance from '../uttils/axiosInstance';
import type { Author } from '@/entities/Author';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isAuthenticated: false,
    author: null as null|Author,
    user: null as null | { userId: string, nickname: string; email: string , avatarUrl: string},
  }),
  actions: {
    async signUp(payload: { username: string; email: string; password: string }) {
      try {
        const response = await axiosInstance.post(`/auth/signUp`, payload);
        const data = await response.data;
        this.user = data;
        this.isAuthenticated = true
        return true;
      } catch (error) {
        console.error('Sign-up failed:', error.response?.data || error.message);
        throw error;
      }
    },

    async signIn(payload: { email: string; password: string }) {
      try {
        const response = await axiosInstance.post(`/auth/signIn`, payload);
        this.user = response.data;
        this.isAuthenticated = true
        return true;
      } catch (error) {
        console.error('Sign-in failed:', error.response?.data || error.message);
        throw error;
      }
    },

    async logout() {
      await axiosInstance.post(`/auth/logout`, {}, { withCredentials: true });
      this.user = null;
      this.isAuthenticated = false
    },

    async requestResetPassword(email: string, baseUrl: string){
      await axiosInstance.post(`/auth/request-reset?email=${email}&frontEndResetUrl=${baseUrl}`, {});
    },

    async resetPassword(token: string, password: string){
      await axiosInstance.post(`/auth/reset?token=${token}&newPassword=${password}`, {});
    },

    async fetchUserData(userId: string){
      try {
        const response = await axiosInstance.get(`/users/public/${userId}`);
        this.author = response.data as Author
      } catch (error) {
        console.warn('User not fetched:', error.response?.data || error.message);
      }
    },

    async initialize() {
      try {
        const response = await axiosInstance.get(`/users/currentUser`, { withCredentials: true });
        this.user = response.data;
        this.isAuthenticated = true;
      } catch (error) {
        console.warn('User not authenticated or session expired:', error.response?.data || error.message);
        this.user = null;
        this.isAuthenticated = false;
      }
    },

    // New method to update the user's profile image
    async updateProfileImage(imageFile: File) {
      try {
        const formData = new FormData();
        formData.append('image', imageFile);

        const response = await axiosInstance.patch('/users/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });

        this.user = response.data;
      } catch (error) {
        console.error('Failed to update profile image:', error.response?.data || error.message);
        throw error;
      }
    },

    // New method to update the user's nickname
    async updateNickname(nickname: string) {
      try {
        const response = await axiosInstance.patch(`/users/nickname?nickname=${nickname}`, { });
        this.user = response.data;
      } catch (error) {
        console.error('Failed to update nickname:', error.response?.data || error.message);
        throw error;
      }
    },

    // Method to reset the stream
    async resetStream(streamName: string): Promise<string> {
      try {
        const res = await axiosInstance.post(`/streams?name=${streamName}`, { name: streamName });
        return res.data
      } catch (error) {
        console.error('Failed to reset stream:', error.response?.data || error.message);
        throw error;
      }
    },

    // Method to update the stream name
    async updateStream(streamName: string): Promise<string> {
      try {
        const res = await axiosInstance.patch(`/streams?name=${streamName}`, { name: streamName });
        return res.data
      } catch (error) {
        console.error('Failed to update stream:', error.response?.data || error.message);
        throw error;
      }
    },
  },
});
