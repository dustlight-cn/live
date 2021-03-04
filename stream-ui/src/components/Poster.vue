<template>
  <q-card>
    <q-img v-if="error || room==null || room != null && room.id == null && src==null"
           class="bg-grey-5"
           :ratio="16/9">
      <slot/>
    </q-img>
    <q-img
      v-else
      :src="computedSrc" :ratio="16/9" :hidden="isLoading()"
      @load="onLoad"
      @error="onError">
      <template v-slot:default>
        <slot/>
      </template>
      <template v-slot:loading>
        <q-skeleton class="full-width full-height"/>
      </template>
    </q-img>
  </q-card>
</template>

<script>
export default {
  name: "Poster",
  props: {
    room: Object,
    src: String
  },
  data() {
    return {
      error: false,
      loading: true
    }
  },
  methods: {
    isLoading() {
      return this.room == null || this.loading && this.room.id != null
    },
    onError() {
      this.error = true
    },
    onLoad() {
      this.loading = false
    }
  },
  computed: {
    computedSrc() {
      if (this.room == null || this.room.id == null)
        return this.src;
      return this.$cfg.resourceHost + "/live/" + this.room.id + "/preview.png";
    }
  },
  watch: {
    src() {
      this.error = false
      this.loading = true
    },
    "room.id"() {
      this.error = false
      this.loading = true
    }
  }
}
</script>

<style scoped>

</style>
