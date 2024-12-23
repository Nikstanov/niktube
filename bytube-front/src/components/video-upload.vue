<template>
  <v-card>
    <v-card-title>Upload Video</v-card-title>
    <v-card-text>
      <v-form ref="uploadForm" v-model="valid" @submit.prevent="submitForm">
        <v-file-input
          v-model="videoFile"
          label="Select Video File"
          accept="video/*"
          required
        />
        <v-file-input
          v-model="previewFile"
          label="Select Preview Image"
          accept="image/*"
          required
        />
        <v-text-field
          v-model="name"
          label="Video Name"
          required
        />
        <v-textarea
          v-model="description"
          label="Description"
          required
        />
        <v-checkbox
          v-model="isPublic"
          label="Make this video public"
        />
      </v-form>
    </v-card-text>
    <v-card-actions v-if="!isLoading">
      <v-btn color="primary" @click="submitForm">Upload</v-btn>
      <v-btn text @click="$emit('close')">Cancel</v-btn>
    </v-card-actions>
    <v-row v-if="isLoading" justify="center">
      <v-progress-circular indeterminate />
    </v-row>
  </v-card>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import axiosInstance from '../uttils/axiosInstance';

const isLoading = ref(false);
const valid = ref(false);
const videoFile = ref<File | null>(null);
const previewFile = ref<File | null>(null);
const name = ref('');
const description = ref('');
const isPublic = ref(false);

const submitForm = async () => {
  isLoading.value = true
  if (!valid.value || !videoFile.value || !previewFile.value) {
    console.error('Validation failed or files not selected');
    return;
  }

  const formData = new FormData();
  formData.append('file', videoFile.value);
  formData.append('preview', previewFile.value);
  formData.append('name', name.value);
  formData.append('description', description.value);

  try {
    const response = await axiosInstance.post('/users/upload?open=' + String(isPublic.value), formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    console.log('Upload successful, video ID:', response.data);
    $emit('close'); // Закрыть диалог после успешной загрузки
  } catch (error) {
    console.error('Upload failed:', error);
  }
  isLoading.value = false
};
</script>
