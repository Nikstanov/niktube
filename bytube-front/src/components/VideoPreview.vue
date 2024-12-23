<template>
  <v-card
    width="240"
    class="video-preview"
    outlined
    @click="navigateToVideo"
  >
    <!-- Video Thumbnail -->
    <v-img
      :src="video.thumbnail"
      alt="Video thumbnail"
      aspect-ratio="16/9"
      class="video-thumbnail"
      contain
    />

    <!-- Video Details -->
    <v-card-text class="video-details">
      <div class="video-title">{{ video.title }}</div>
      <div class="video-meta">
        <span class="video-author">
          By
          <a :href="`/author/${video.owner.userId}`" class="author-link">
            {{ video.owner.nickname }}
          </a>
        </span>
        <span class="video-views">{{ video.views }} views</span>
        <span class="video-time">Added: {{ video.created }}</span>
      </div>
    </v-card-text>
  </v-card>
</template>


<script lang="ts" setup>
import type { Video } from "../entities/Video";
import { defineProps } from "vue";
import router  from './../router/index';

// Props for the video details
const props = defineProps({
  video: {
    type: Object as () => Video,
    required: true,
  },
});

// Router instance

// Method to navigate to the video page
const navigateToVideo = () => {
  if (props.video.videoId) {
    router.push(`/video/${props.video.videoId}`);
  }
};
</script>


<style scoped lang="scss">
.video-preview {
  cursor: pointer;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.05);
  }
}

.video-thumbnail {
  width: 100%; /* Занимает всю ширину карточки */
  aspect-ratio: 16 / 9; /* Соотношение сторон 16:9 */
  object-fit: cover; /* Миниатюра покрывает заданную область */
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.video-details {
  padding: 12px;
  font-size: 0.875rem;
  text-align: left;

  .video-title {
    font-weight: 600;
    margin-bottom: 8px;
    color: rgba(var(--v-theme-on-surface), 1);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .video-meta {
    display: flex;
    flex-direction: column;
    font-size: 0.75rem;
    color: rgba(var(--v-theme-on-surface), 0.6);

    .video-author,
    .video-views,
    .video-time {
      margin-bottom: 4px;
    }

    .author-link {
      color: inherit;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>


