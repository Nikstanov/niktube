<template>
  <v-card
    width="240"
    class="stream-preview"
    outlined
    @click="navigateToStream"
  >
    <!-- Stream Details -->
    <v-card-text class="stream-details">
      <div class="stream-title">{{ stream.name }}</div>
      <div class="stream-meta">
        <span class="stream-owner">
          Hosted by
          <a :href="`/author/${stream.owner.userId}`" class="owner-link">
            {{ stream.owner.nickname }}
          </a>
        </span>
        <span class="stream-time">Started: {{ formatDate(stream.startTime) }}</span>
      </div>
    </v-card-text>
  </v-card>
</template>


<script lang="ts" setup>
import type { Stream } from "../entities/Stream";
import { defineProps } from "vue";
import router from "../router/index";

// Props for the stream details
const props = defineProps({
  stream: {
    type: Object as () => Stream,
    required: true,
  },
});

// Method to navigate to the stream page
const navigateToStream = () => {
  if (props.stream.id) {
    router.push(`/stream/${props.stream.id}`);
  }
};

// Utility function to format dates
const formatDate = (date: string): string => {
  return new Intl.DateTimeFormat("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
  }).format(new Date(date));
};
</script>

<style scoped lang="scss">
.stream-preview {
  cursor: pointer;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.05);
  }
}

.stream-thumbnail {
  width: 100%; /* Ensure full width of the card */
  height: 160px; /* Fixed height for uniformity */
  object-fit: cover;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.stream-details {
  padding: 12px;
  font-size: 0.875rem;
  text-align: left; /* Align text to the left */

  .stream-title {
    font-weight: 600;
    margin-bottom: 8px;
    color: rgba(var(--v-theme-on-surface), 1);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .stream-meta {
    display: flex;
    flex-direction: column;
    font-size: 0.75rem;
    color: rgba(var(--v-theme-on-surface), 0.6);

    .stream-owner,
    .stream-time {
      margin-bottom: 4px;
    }

    .owner-link {
      color: inherit;
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

</style>

