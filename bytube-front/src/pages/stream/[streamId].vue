<template>
  <div class="stream-page">
    <!-- Stream Player -->
    <v-card
      class="stream-player-card"
      outlined
    >
      <video
        ref="streamPlayer"
        class="video-js vjs-big-play-centered"
        controls
      />
    </v-card>

    <!-- Loading State -->
    <div
      v-if="!stream"
      class="loading"
    >
      <p>Loading stream...</p>
    </div>

    <!-- Stream Details -->
    <div
      v-if="stream"
      class="stream-details"
    >
      <div class="stream-header">
        <h1 class="stream-title">
          {{ stream.name }}
        </h1>
        <div class="stream-meta-top">
          <img
            :src="stream.owner.avatarUrl"
            alt="Owner Avatar"
            class="owner-avatar"
          >
          <a
            :href="`/author/${stream.owner.userId}`"
            class="owner-link"
          >
            {{ stream.owner.nickname }}
          </a>
          <span class="stream-start-time">
            Started at: {{ formatDate(stream.startTime) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>


<script lang="ts" setup>
import 'video.js/dist/video-js.css';
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRoute } from "vue-router";
import { useStreamStore } from "../../stores/stream";
import type { Stream } from "../../entities/Stream";
import videojs from "video.js";
import 'videojs-contrib-quality-levels';
import qalitySelectorHls from 'videojs-quality-selector-hls';

const stream = ref<Stream | null>(null);
const streamPlayer = ref(null); // Reference for the stream player DOM element
const player = ref(null); // Holds the Video.js player instance

const route = useRoute();
const streamStore = useStreamStore();

const streamSource = computed(() => {
  return stream.value
    ? `http://localhost:8080/streams/hls/${route.params.streamId}.m3u8`
    : '';
});

onMounted(async () => {
  const streamId = route.params.streamId as string;

  if (!streamId) {
    console.error("No streamId found in the route.");
    return;
  }

  stream.value = await streamStore.getStreamById(streamId);

  videojs.registerPlugin('qalitySelectorHls', qalitySelectorHls);
  if (streamPlayer.value) {
    player.value = videojs(streamPlayer.value, {
      controls: true,
      autoplay: true,
      responsive: true,
      preload: 'auto',
      fluid: true,
      playbackRates: [0.5, 1, 1.5, 2],
      sources: [
        {
          src: streamSource.value,
          type: 'application/x-mpegURL', // HLS stream
        },
      ],
      plugins: {
        qalitySelectorHls: {}
      }
    }, () => {
      console.log('Player is ready for live stream');
    });
  }
});

onUnmounted(() => {
  // Dispose of player on unmount
  if (streamPlayer.value) {
    player.value.dispose();
    player.value = null;
  }
});

// Utility function to format dates
const formatDate = (date: Date | string): string => {
  return new Intl.DateTimeFormat("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
  }).format(new Date(date));
};
</script>

<style lang="scss">

.stream-page {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 2rem;

  .stream-player-card {
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

  .stream-details {
    width: 60%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;

    .stream-header {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      margin-bottom: 1.5rem;

      .stream-title {
        font-size: 2rem;
        font-weight: bold;
        color: #333;
        margin-bottom: 0.5rem;
      }

      .stream-meta-top {
        display: flex;
        align-items: center;
        gap: 1rem;

        .owner-avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          object-fit: cover;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .owner-link {
          color: #007BFF;
          font-weight: bold;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }

        .stream-start-time {
          color: #666;
          font-size: 0.9rem;
        }
      }
    }
  }
}

</style>


