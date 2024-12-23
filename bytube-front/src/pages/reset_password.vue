<template>
  <v-container class="fill-height d-flex align-center justify-center">
    <v-card
      width="400"
      class="pa-4"
    >
      <v-card-title>{{ 'Reset' }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field
            v-model="password"
            label="Password"
            type="password"
            required
          />
          <v-text-field
            v-model="confirmPassword"
            label="Confirm password"
            type="password"
            required
          />
          <v-btn
            type="submit"
            color="blue"
            block
          >
            Reset
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script lang="ts" setup>
  import { useAuthStore } from './../stores/auth';
  import { onMounted, ref } from 'vue';
  import router  from './../router/index';
import { useRoute } from 'vue-router';

  const password = ref('');
  const confirmPassword = ref('');
  const token = ref('');

  onMounted(() => {
    const route = useRoute();
    token.value = route.query.token as string || '';  // Get the token from the query parameter
  });


  const handleSubmit = async () => {
    // Handle form submission (e.g., API calls or store updates)
    try {
      await useAuthStore().resetPassword(token.value, password.value );
      router.replace("/auth");
    } catch (error) {
      console.error('Error during reset:', error);
    }

    // Clear form fields
    password.value = '';
  };
</script>
