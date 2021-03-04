<template>
  <q-badge :color="color" :label="label"/>
</template>

<script>
export default {
  name: "StatusBadge",
  props: {
    data: Boolean | Object
  },
  computed: {
    val() {
      return this.data ? (this.data.streaming != null ? this.data.streaming : this.data) : false;
    },
    color() {
      return this.val ? 'red' : 'grey';
    },
    label() {
      return this.$t(this.val ? 'ONLINE' : 'OFFLINE');
    }
  }
}
</script>

<style scoped>

</style>
