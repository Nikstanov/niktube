<template>
  <v-container>
    <!-- User Info Section -->
    <v-row
      justify="space-between"
      class="mb-10"
    >
      <v-col cols="auto">
        <img
          :src="profileUser?.avatarUrl || '/default-avatar.png'"
          alt="Author Avatar"
          class="profile-image"
        >
      </v-col>

      <!-- Profile Info Column -->
      <v-col>
        <div class="profile-details">
          <h2>{{ profileUser?.nickname }}</h2>
        </div>
      </v-col>
    </v-row>

    <p v-if="!stream">No active stream found</p>
    <!-- User Stream Section -->
    <div v-else>
      <h3>
        Stream
      </h3>
      <v-row>
        <stream-preview :stream="stream" />
      </v-row>
    </div>

    <p v-if="!videos.length">No videos available</p>
    <div v-else>
      <!-- User Videos Section -->
      <h3>Videos</h3>
      <v-row>
        <v-col
          v-for="(video, _) in videos"
          :key="video.videoId"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <video-preview :video="video" />
        </v-col>
      </v-row>
    </div>

    <v-row
      v-if="videoLoading"
      justify="center"
    >
      <v-progress-circular indeterminate />
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useVideoStore } from '@/stores/video';
import { useStreamStore } from '@/stores/stream';
import { useRoute } from 'vue-router';
import type { Author } from '@/entities/Author';

const route = useRoute();
const authStore = useAuthStore();
const videoStore = useVideoStore();
const streamStore = useStreamStore();

const stream = ref(null);

// Computed property to bind to Pinia store
const profileUser = computed(() => authStore.author);

// Fetch profile data on mount
onMounted(() => {
  const authorId = route.params.authorId as string
  authStore.fetchUserData(authorId)
  console.log(profileUser.value)
  fetchUserContent(authorId);
});

const fetchUserContent = async (userId: string) => {
  await videoStore.fetchUserVideos(userId);
  await streamStore.fetchUserStreams(userId);
};

// Bind store data
const videos = computed(() => videoStore.videos);
const videoLoading = computed(() => videoStore.loading);

</script>

<style scoped lang="scss">

h2 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

p {
  color: rgba(var(--v-theme-on-surface), 0.6);
}

.profile-image {
  width: 10em; /* Set the desired size */
  height: 10em; /* Ensure square shape */
  object-fit: cover; /* Ensures the image scales and fills the square without distortion */
  border: 2px solid rgba(var(--v-theme-on-surface), 0.2); /* Optional: Adds a border */
}

.profile-details h2 {
  font-size: 2rem;
  margin: 0;
}

.profile-details p {
  color: rgba(var(--v-theme-on-surface), 0.6);
  margin: 0;
}

h3 {
  font-size: 1.5rem;
  margin-bottom: 0.75rem;
}

p {
  color: rgba(var(--v-theme-on-surface), 0.6);
  margin-bottom: 1rem;
}

.v-container {
  padding: 2rem;
}

.v-row {
  margin-bottom: 1.5rem;
}

.video-card,
.stream-card {
  cursor: pointer;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.05);
  }
}

.video-thumbnail {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.video-title,
.stream-title {
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-meta,
.stream-meta {
  font-size: 0.875rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
}
</style>
