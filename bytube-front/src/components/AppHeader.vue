<template>
  <v-app-bar
    app
    flat
  >
    <v-container class="d-flex align-center mt-4">
      <!-- App Logo -->
      <router-link
        to="/"
        class="text-decoration-none d-flex align-center"
      >
        <img
          src="/logo.png"
          alt="App Logo"
          style="max-width: 5em"
        >
        <h1 class="app-title">
          BYTUBE
        </h1>
      </router-link>

      <!-- Spacer to push the links to the far-right -->
      <v-spacer />

      <!-- Navigation Links -->
      <router-link
        v-if="!isAuthenticated"
        to="/auth"
        class="nav-link"
      >
        <v-btn
          variant="text"
          color="primary"
        >
          Sign In
        </v-btn>
      </router-link>

      <!-- Dropdown Menu for Authenticated Users -->
      <v-menu
        v-else
        offset-y
        :activator="activatorRef"
        class="menu-wrapper"
      >
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            variant="text"
            class="profile-button"
          >
            {{ user?.nickname || "Profile" }}
          </v-btn>
        </template>
        <v-list>
          <v-list-item to="/userProfile">
            Profile
          </v-list-item>
          <v-list-item @click="logout">
            Logout
          </v-list-item>
        </v-list>
      </v-menu>
    </v-container>
  </v-app-bar>
</template>

<script lang="ts" setup>
import { useAuthStore } from "./../stores/auth";
import { computed } from "vue";
import { useRouter } from "vue-router";

// Access auth store
const authStore = useAuthStore();
const router = useRouter();

// Reactive computed properties
const isAuthenticated = computed(() => authStore.isAuthenticated);
const user = computed(() => authStore.user);

// Logout handler
const logout = () => {
  authStore.logout();
  router.push("/"); // Redirect to home after logout
};
</script>

<style scoped lang="scss">
/* App title styling */
.app-title {
  font-size: 1.5rem;
  color: rgba(var(--v-theme-on-background), 0.9);
  text-transform: uppercase;
  margin: 0;
}

/* Navigation link styling */
.nav-link :deep(.v-btn) {
  font-size: 1rem;
  font-weight: 500;
  text-transform: capitalize;
  transition: color 0.2s ease-in-out;

  &:hover {
    color: rgba(25, 118, 210, 1);
  }
}

/* Header styling */
.v-app-bar {
  box-shadow: none !important; /* Remove shadow */
}

/* Profile button */
.profile-button {
  font-size: 1rem;
  font-weight: 500;
  text-transform: capitalize;
}

/* Compact dropdown menu */
.menu-wrapper :deep(.v-list) {
  max-width: 150px;
}

.menu-wrapper :deep(.v-list-item) {
  padding: 8px 16px; /* Adjust padding for items */
  font-size: 0.9rem; /* Slightly reduce font size */
}

/* Logo image */
.v-img {
  display: inline-block;
  vertical-align: middle;
}

/* Ensure the "Sign In" button aligns to the right */
.v-spacer + .nav-link {
  margin-left: auto;
}
</style>
