<template>
  <v-container>
    <h3>Stream Settings</h3>

    <v-form>
      <!-- Stream URL -->
      <v-text-field
        v-model="streamUrl"
        label="Stream URL"
        readonly
        outlined
      />

      <!-- Stream Key -->
      <v-text-field
        v-model="streamKey"
        label="Stream Key"
        readonly
        outlined
      />

      <!-- Stream Name -->
      <v-text-field
        v-model="streamName"
        label="Stream Name"
        outlined
      />

      <!-- Actions -->
      <v-row justify="space-between" class="mt-4">
        <!-- Update Stream Name -->
        <v-btn color="primary" @click="handleUpdateStream">
          Update Stream Name
        </v-btn>

        <!-- Reset Stream Key -->
        <v-btn color="error" @click="handleResetStream">
          Reset Stream Key
        </v-btn>
      </v-row>
    </v-form>
  </v-container>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();

// Stream data
const streamUrl = ref<string>('rtmp://localhost:1935/live'); // Stream URL
const streamKey = ref<string>(''); // Stream Key
const streamName = ref<string>(''); // Stream Name

// Fetch initial stream details on mount
onMounted(async () => {
  // Assuming you have an API to fetch stream details
  // const userStream = await authStore.fetchUserStream();
  streamUrl.value = 'rtmp://localhost:1935/live';
  streamKey.value = "";
  streamName.value = "";
});

// Handle stream name update
const handleUpdateStream = async () => {
  if (!streamName.value.trim()) return;

  try {
    const newName = await authStore.updateStream(streamName.value.trim());
    streamName.value = newName
    console.log('Stream name updated successfully');
  } catch (error) {
    console.error('Failed to update stream name:', error);
  }
};

// Handle resetting the stream key
const handleResetStream = async () => {
  try {
    const newStream = await authStore.resetStream(streamName.value);
    streamKey.value = newStream; // Update with the new stream key
    console.log('Stream key reset successfully');
  } catch (error) {
    console.error('Failed to reset stream key:', error);
  }
};
</script>

<style scoped>
.stream-settings-container {
  max-width: 600px;
  margin: 2rem auto;
}

h3 {
  color: #424242;
}

.input-style {
  border-radius: 8px;
}

.v-btn.btn-style {
  text-transform: none;
  font-weight: 500;
  padding: 8px 16px;
}

.v-btn.btn-style:hover {
  filter: brightness(1.1);
  transition: all 0.2s ease-in-out;
}
</style>
