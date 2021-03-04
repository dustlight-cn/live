<template>
  <div :class="clazz">
    <slot name="before"/>
    <transition
      appear
      enter-active-class="animated fadeIn"
      leave-active-class="animated fadeOut"
      v-for="(item,index) in data" :key="'xs-'+index"
    >
      <div class="xs" :style="dataSize>1?'width: 50%':'width: 100%'">
        <slot name="default" v-bind="{item:item,index:index,type:0}"/>
      </div>
    </transition>
    <transition
      appear
      enter-active-class="animated fadeIn"
      leave-active-class="animated fadeOut"
      v-for="(item,index) in data" :key="'sm&md-'+index"
    >
      <div class="sm" :style="dataSize>2?'width: 33.33%':'width: 50%'">
        <slot name="default" v-bind="{item:item,index:index,type:1}"/>
      </div>
    </transition>
    <transition
      appear
      enter-active-class="animated fadeIn"
      leave-active-class="animated fadeOut"
      v-for="(item,index) in data" :key="'gt-md-'+index"
    >
      <div class="gt-sm" style="width: 250px">
        <slot name="default" v-bind="{item:item,index:index,type:2}"/>
      </div>
    </transition>
    <slot name="after"/>
  </div>
</template>

<script>
export default {
  name: "FitGrid",
  props: {
    data: Array
  },
  computed: {
    dataSize() {
      return this.data && this.data.length ? this.data.length : 0;
    },
    clazz() {
      return "row justify-" + (this.dataSize > 1 ? "center" : "start")
    }
  }
}
</script>

<style scoped>

</style>
