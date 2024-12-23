<template>
  <v-container>
    <!-- User Info Section -->

    <v-row
      justify="space-between"
      class="mb-4"
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
          <p>{{ profileUser?.email }}</p>
        </div>
      </v-col>

      <!-- Action Menu -->
      <v-menu offset-y>
        <template #activator="{ isActive, props }">
          <v-btn
            v-bind="props"
            color="primary"
            text
            v-on="isActive"
            @click.stop
          >
            Actions
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="openCreateVideoDialog">
            <v-list-item-title>Create Video</v-list-item-title>
          </v-list-item>
          <v-list-item @click="openEditProfileImageDialog">
            <v-list-item-title>Change Profile Image</v-list-item-title>
          </v-list-item>
          <v-list-item @click="openEditProfileNameDialog">
            <v-list-item-title>Change Profile Name</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-row>

    <!-- User Stream Section -->
    <h3 v-if="stream">
      Stream
    </h3>
    <p v-else>
      No active stream
    </p>
    <StreamOptionsForm />
    <v-row v-if="stream">
      <stream-preview :stream="stream" />
    </v-row>

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
        <v-btn
          class="video-button"
          text
          @click="deleteVideo(video.videoId)"
        >
          Delete
        </v-btn>
        <v-btn
          class="video-button"
          text
          @click="patchVideo(video.videoId)"
        >
          Make public/private
        </v-btn>
      </v-col>
    </v-row>

    <v-row
      v-if="videoLoading"
      justify="center"
    >
      <v-progress-circular indeterminate />
    </v-row>

    <!-- Create Video Dialog -->
    <v-dialog v-model="createVideoDialog">
      <video-upload @close="createVideoDialog = false" />
    </v-dialog>

    <!-- Profile Image Dialog -->
    <v-dialog v-model="editProfileImageDialog">
      <v-card>
        <v-card-title>Change Profile Image</v-card-title>
        <v-card-text>
          <v-file-input
            v-model="newProfileImage"
            accept="image/*"
            label="Select a new profile image"
            outlined
          />
        </v-card-text>
        <v-card-actions>
          <v-btn
            color="primary"
            @click="updateProfileImage"
          >
            Save
          </v-btn>
          <v-btn
            text
            @click="editProfileImageDialog = false"
          >
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Profile Nickname Dialog -->
    <v-dialog v-model="editProfileNameDialog">
      <v-card>
        <v-card-title>Change Nickname</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="newNickname"
            label="New Nickname"
            outlined
          />
        </v-card-text>
        <v-card-actions>
          <v-btn
            color="primary"
            @click="updateNickname"
          >
            Save
          </v-btn>
          <v-btn
            text
            @click="editProfileNameDialog = false"
          >
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>


<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useVideoStore } from '../stores/video';
import { useStreamStore } from '../stores/stream';
import StreamOptionsForm from '../components/StreamOptionsForm.vue';

const authStore = useAuthStore();
const videoStore = useVideoStore();
const streamStore = useStreamStore();

const createVideoDialog = ref(false);
const editProfileNameDialog = ref(false);
const editProfileImageDialog = ref(false);

// Profile user data (this should come from route params or API)
const profileUser = ref<{ userId: string, nickname: string; email: string , avatarUrl: string} | null>(null);

// Stream data (unique for a user)
const stream = ref(null);
const newProfileImage = ref<File | null>(null);
const newNickname = ref<string>('');

// Fetch profile data on mount
onMounted(() => {
  profileUser.value = authStore.user// Implement logic to fetch userId from route
  fetchUserContent(profileUser.value!.userId);
});

const updateProfileImage = async () => {
  if (!newProfileImage.value) return;

  try {
    // Call the store method to update the profile image
    await authStore.updateProfileImage(newProfileImage.value);

    // Update local profile data
    profileUser.value = authStore.user;

    // Close the dialog
    editProfileImageDialog.value = false;
  } catch (error) {
    console.error('Failed to update profile image', error);
  }
};

const updateNickname = async () => {
  if (!newNickname.value.trim()) return;

  try {
    // Call the store method to update the nickname
    await authStore.updateNickname(newNickname.value.trim());

    // Update local profile data
    profileUser.value = authStore.user;

    // Close the dialog
    editProfileNameDialog.value = false;
  } catch (error) {
    console.error('Failed to update nickname', error);
  }
};


const fetchUserContent = async (userId: string) => {
  await videoStore.fetchUserVideos(userId);
  await streamStore.fetchUserStreams(userId);
};

const deleteVideo = async (videoId: string) => {
  try {
    // Call the store method to update the nickname
    await videoStore.deleteVideo(videoId);
    await videoStore.fetchUserVideos(profileUser.value!.userId);

  } catch (error) {
    console.error('Failed to deleting video', error);
  }
}

const patchVideo = async (videoId: string) => {
  try {
    // Call the store method to update the nickname
    await videoStore.updateStatus(videoId);
    await videoStore.fetchUserVideos(profileUser.value!.userId);

  } catch (error) {
    console.error('Failed to updating video', error);
  }
}

// Open modals
const openCreateVideoDialog = () => {
  createVideoDialog.value = true;
};

const openEditProfileImageDialog = () => {
  editProfileImageDialog.value = true;
};

const openEditProfileNameDialog = () => {
  editProfileNameDialog.value = true;
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

.video-card,
.stream-card {
  cursor: pointer;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: scale(1.05);
  }
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

.video-button{
  margin-top: 1em;
  width: 15em;
}
</style>
