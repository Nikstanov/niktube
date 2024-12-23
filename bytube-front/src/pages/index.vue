<template>
  <v-container>
    <v-tabs v-model="activeTab" grow class="tabs">
      <v-tab>Videos</v-tab>
      <v-tab>Streams</v-tab>
    </v-tabs>

    <v-tabs-items v-model="activeTab">
      <!-- Videos Tab -->
      <v-tab-item v-if="activeTab == 0">
        <!-- Search Field -->
        <v-text-field
          v-model="searchQuery"
          label="Search videos..."
          outlined
          clearable
          prepend-inner-icon="mdi-magnify"
          class="mb-4 search-field"
          @keyup.enter="handleSearch"
        />

        <v-row>
          <v-col
            v-for="(video, _) in videoStore.videos"
            :key="video.videoId"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <video-preview :video="video" />
          </v-col>
        </v-row>

        <!-- Loading Indicator -->
        <v-row v-if="videoStore.loading" justify="center">
          <v-progress-circular indeterminate />
        </v-row>
      </v-tab-item>

      <!-- Streams Tab -->
      <v-tab-item v-if="activeTab == 1">
        <v-row>
          <v-col
            v-for="(stream, _) in streamStore.streams"
            :key="stream.id"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <stream-preview :stream="stream" />
          </v-col>
        </v-row>

        <v-row v-if="streamStore.loading" justify="center">
          <v-progress-circular indeterminate />
        </v-row>
      </v-tab-item>
    </v-tabs-items>

    <!-- Dialog for Search Results -->
    <v-dialog v-model="showDialog" max-width="800">
      <v-card>
        <v-card-title class="headline">Search Results</v-card-title>

        <v-card-text>
          <v-row v-if="videoStore.loading" justify="center">
            <v-progress-circular indeterminate />
          </v-row>

          <v-row v-else-if="videoStore.videos.length > 0">
            <v-col
              v-for="(video, _) in videoStore.videos"
              :key="video.videoId"
              cols="12"
              sm="6"
              md="4"
              lg="3"
            >
              <video-preview :video="video" />
            </v-col>
          </v-row>

          <p v-else>No videos found for "{{ searchQuery }}".</p>
        </v-card-text>

        <v-card-actions>
          <v-btn color="primary" text @click="handleCloseSearch">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue';
import { useVideoStore } from '../stores/video';
import { useStreamStore } from '../stores/stream';

const videoStore = useVideoStore();
const streamStore = useStreamStore();

const activeTab = ref(0);
const searchQuery = ref('');
const showDialog = ref(false);

// Fetch videos and streams on mount
onMounted(() => {
  videoStore.resetVideos();
  videoStore.fetchVideos();
  streamStore.fetchStreams();
});

const handleCloseSearch = () => {
  showDialog.value = false
  videoStore.resetVideos();
  videoStore.fetchVideos();
}

// Infinite scroll logic for videos and streams
const onScroll = () => {
  const nearBottom =
    window.innerHeight + window.scrollY >= document.body.offsetHeight - 300;

  if (activeTab.value === 0 && nearBottom && videoStore.hasMore) {
    videoStore.fetchVideos();
  } else if (activeTab.value === 1 && nearBottom && streamStore.hasMore) {
    streamStore.fetchStreams();
  }
};

// Handle search action
const handleSearch = () => {
  if (activeTab.value === 0) {
    videoStore.resetVideos();
    videoStore.findVideos(searchQuery.value);
    showDialog.value = true; // Show dialog with search results
  }
};

window.addEventListener('scroll', onScroll);
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll);
});
</script>

<style scoped lang="scss">
.tabs {
  margin-bottom: 2em;
}

.search-field {
  margin: 0 auto;
  transition: all 0.3s ease;

  &:hover,
  &:focus-within {
    border-color: #1976d2;
  }
}

.v-card-title {
  color: #1976d2;
  font-weight: bold;
}
</style>
