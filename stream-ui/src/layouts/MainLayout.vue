<template>
  <q-layout view="HHh Lpr lFf">
    <q-header class="bg-white text-black" elevated>
      <q-toolbar>
        <q-btn icon="home" flat :to="{name:'index'}">
          <q-toolbar-title class="text-weight-bold">
            {{ $t('siteName') }}
          </q-toolbar-title>
        </q-btn>
        <q-space/>
        <avatar-button/>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view/>
    </q-page-container>

  </q-layout>
</template>

<script lang="ts">
import AvatarButton from "components/AvatarButton.vue";

export default {
  name: 'MainLayout',
  components: {AvatarButton}
}
</script>
