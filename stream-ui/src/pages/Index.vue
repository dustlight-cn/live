<template>
  <q-page>
    <q-drawer
      show-if-above
      content-class="bg-grey-1"
      bordered
    >
      <q-tabs
        vertical
        v-model="tag"
        indicator-color="primary"
        active-color="primary"
        class="bg-white shadow-2 text-black"
      >
        <q-route-tab v-for="(menu,index) in menus" :key="'menu-'+index"
                     no-caps
                     :name="menu.name"
                     :label="$t('menu.'+menu.name)"
                     :icon="menu.icon"
                     :to="menu.to&&menu.to.trim().length>0?{name:menu.to}:null"
        />
      </q-tabs>
    </q-drawer>

    <router-view/>

    <q-footer elevated v-if="$q.screen.lt.md">
      <q-tabs
        mobile-arrows
        v-model="tag"
        inline-label
        switch-indicator
        indicator-color="primary"
        active-color="primary"
        class="bg-white shadow-2 text-black"
      >
        <q-route-tab v-for="(menu,index) in menus" :key="'menu-'+index"
                     no-caps
                     :name="menu.name"
                     :label="$t('menu.'+menu.name)"
                     :icon="menu.icon"
                     :to="menu.to&&menu.to.trim().length>0?{name:menu.to}:null"
        />
      </q-tabs>
    </q-footer>
  </q-page>
</template>

<script lang="ts">
import menus from 'src/menus'

export default {
  name: 'PageIndex',
  data() {
    return {
      tag: 'home',
      menus: menus.main
    }
  }
}
</script>
