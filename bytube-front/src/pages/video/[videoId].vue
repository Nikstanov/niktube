<template>
  <div class="video-page">
    <!-- Video Player -->
    <v-card
      class="video-player-card"
      outlined
    >
      <video
        ref="videoPlayer"
        class="video-js vjs-big-play-centered"
        controls
      />
    </v-card>

    <!-- Loading State -->
    <div
      v-if="!video"
      class="loading"
    >
      <p>Loading video...</p>
    </div>

    <!-- Video Details -->
    <div
      v-if="video"
      class="video-details"
    >
      <div class="video-header">
        <h1 class="video-title">
          {{ video.title }}
        </h1>
        <div class="video-meta-top">
          <img
            :src="video.owner.avatarUrl || '/default-avatar.png'"
            alt="Author Avatar"
            class="author-avatar"
          >
          <a
            :href="`/author/${video.owner.userId}`"
            class="author-link"
          >
            {{ video.owner.nickname }}
          </a>
          <span class="video-views">
            {{ video.views }} views
          </span>
        </div>
      </div>

      <div class="video-body">
        <p class="video-description">
          {{ video.description }}
        </p>
        <span class="video-time">
          Added: {{ formatDate(video.created) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import 'video.js/dist/video-js.css';
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRoute } from "vue-router";
import { useVideoStore } from "../../stores/video";
import type { Video } from "../../entities/Video";
import videojs from "video.js";
import 'videojs-contrib-quality-levels';
import qalitySelectorHls from 'videojs-quality-selector-hls';
import { customXhrSetup } from '../../uttils/customXhrSetup';

const video = ref<Video | null>(null);
const videoPlayer = ref(null); // Reference for the video player DOM element
const player = ref(null); // Holds the Video.js player instance

const route = useRoute();
const videoStore = useVideoStore();

const videoSource = computed(() => {
  return video.value ? `http://localhost:8080/video/hls/${route.params.videoId}/master.m3u8` : '';
});

onMounted(async () => {
  const videoId = route.params.videoId as string;

  if (!videoId) {
    console.error("No videoId found in the route.");
    return;
  }

  video.value = await videoStore.getVideoById(videoId);

  videojs.registerPlugin('qalitySelectorHls', qalitySelectorHls);
  if (videoPlayer.value) {
    player.value = videojs(videoPlayer.value, {
      controls: true,
      autoplay: false,
      responsive: true,
      preload: 'auto',
      fluid: true,
      playbackRates: [0.5, 1, 1.5, 2],
      sources: [
        {
          src: videoSource.value,
          type: 'application/x-mpegURL', // HLS stream
          withCredentials: true
        },
      ],
      plugins: {
        qalitySelectorHls: {}
      }
    }, () => {
      console.log('Player is ready');
    });
  }
});

onUnmounted(() => {
  // Dispose of player on unmount
  if (videoPlayer.value) {
    videoPlayer.value.dispose();
    player.value = null;
  }
});

// Utility function to format dates
const formatDate = (date: Date | string): string => {
  return new Intl.DateTimeFormat("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  }).format(new Date(date));
};
</script>

<style lang="scss">
.video-page {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 2rem;

  .video-player-card {
    width: 60%;
    margin-bottom: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  .loading {
    font-size: 1.2rem;
    color: #888;
    text-align: center;
    width: 100%;
  }

  .video-details {
    width: 60%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;

    .video-header {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      margin-bottom: 1.5rem;

      .video-title {
        font-size: 2rem;
        font-weight: bold;
        color: #333;
        margin-bottom: 0.5rem;
      }

      .video-meta-top {
        display: flex;
        align-items: center;
        gap: 1rem;

        .author-avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          object-fit: cover;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .author-link {
          color: #007BFF;
          font-weight: bold;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }

        .video-views {
          color: #666;
          font-size: 0.9rem;
        }
      }
    }

    .video-body {
      .video-description {
        font-size: 1.1rem;
        color: #555;
        margin-bottom: 0.5rem;
      }

      .video-time {
        font-size: 0.9rem;
        color: #888;
      }
    }
  }
}
</style>
