<template>
  <v-container class="fill-height d-flex align-center justify-center">
    <v-card
      width="400"
      class="pa-4"
    >
      <v-card-title>{{ isSignIn ? 'Sign In' : 'Sign Up' }}</v-card-title>

      <v-card-text v-if="resetPassword">
        <v-form @submit.prevent="handleSubmit">
          <v-text-field
            v-model="email"
            label="Email"
            type="email"
            required
          />
          <v-btn
            type="submit"
            color="blue"
            block
          >
            {{ 'Reset password' }}
          </v-btn>
        </v-form>
      </v-card-text>
      <v-card-text v-else>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field
            v-model="email"
            label="Email"
            type="email"
            required
          />
          <v-text-field
            v-if="!isSignIn"
            v-model="username"
            label="Nickame"
            type="text"
            required
          />
          <v-text-field
            v-model="password"
            label="Password"
            type="password"
            required
          />
          <v-text-field
            v-if="!isSignIn"
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
            {{ isSignIn ? 'Sign In' : 'Sign Up' }}
          </v-btn>
        </v-form>
        <v-btn
          color="red"
          block
          class="mt-4"
          @click="handleGoogleSignIn"
        >
          <v-icon left>mdi-google</v-icon>
          Continue with Google
        </v-btn>
      </v-card-text>
      <v-card-actions v-if="!resetPassword">
        <v-btn
          variant="text"
          @click="toggleMode"
        >
          {{ isSignIn ? "Don't have an account? Sign Up" : "Already have an account? Sign In" }}
        </v-btn>
      </v-card-actions>
      <v-card-actions v-if="isSignIn">
        <v-btn
          variant="text"
          @click="resetMode"
        >
          {{ resetPassword ? "Return to sign In" : "Forget password?" }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script lang="ts" setup>
  import { useAuthStore } from './../stores/auth';
  import { ref } from 'vue';
  import router  from './../router/index';

  const isSignIn = ref(true);
  const resetPassword = ref(false);
  const email = ref('');
  const username = ref('');
  const password = ref('');
  const confirmPassword = ref('');

  const toggleMode = () => {
    isSignIn.value = !isSignIn.value;
  };

  const resetMode = () => {
    resetPassword.value = !resetPassword.value;
  };

  const handleSubmit = async () => {
    // Handle form submission (e.g., API calls or store updates)
    if(resetPassword.value){
      await useAuthStore().requestResetPassword(email.value, "http://localhost:3000/reset_password");
    }
    if (isSignIn.value) {
      try {
        await useAuthStore().signIn({ email: email.value, password: password.value });
        router.replace("/");
      } catch (error) {
        console.error('Error during sign-in:', error);
      }
    } else {
      try {
        await useAuthStore().signUp({ username: username.value, email: email.value, password: password.value });
        router.replace("/");
      } catch (error) {
        console.error('Error during sign-up:', error);
      }
    }

    // Clear form fields
    email.value = '';
    password.value = '';
  };

  // Google OAuth2 Sign-In
  const handleGoogleSignIn = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'
  };

</script>
