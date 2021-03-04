<template>
  <q-img :ratio="ratio" basic>
    <player
      v-if="room && room.id"
      :controls="controls"
      :autoplay="autoplay">
      <hls :mediaTitle="room.name">
        <source :data-src="src">
      </hls>
    </player>
    <q-skeleton v-else type="rect" class="full-width full-height"/>
  </q-img>
</template>

<script>
import {Player, Hls, Ui, LoadingScreen} from '@vime/vue'

export default {
  name: "StreamPlayer",
  components: {Player, Hls, Ui, LoadingScreen},
  props: {
    room: Object,
    ratio: {
      type: Number,
      default() {
        return 16 / 9;
      }
    },
    controls: {
      type: Boolean,
      default() {
        return this.streaming;
      }
    },
    autoplay: {
      type: Boolean,
      default() {
        return this.streaming;
      }
    }
  },
  computed: {
    src() {
      if (this.room == null || this.room.id == null)
        return null;
      return this.$cfg.resourceHost + "/live/" + this.room.id + "/index.m3u8"
    },
    streaming() {
      return this.room && this.room.streaming;
    }
  }
}
</script>

<style scoped>

</style>
