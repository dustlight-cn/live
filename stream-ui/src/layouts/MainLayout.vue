<template>
  <q-layout view="HHh Lpr lFf">
    <q-header class="bg-white text-black" elevated>
      <q-toolbar>
        <q-btn round dense icon="home" flat :to="{name:'index'}"/>
        <q-toolbar-title>
          {{ $t('siteName') }}
        </q-toolbar-title>
        <q-input v-if="isShowSearch"
                 :loading="searching"
                 class="gt-xs full-width q-pr-sm"
                 :debounce="500"
                 style="max-width: 300px;"
                 v-model="text"
                 dense
                 filled
                 clearable
                 :placeholder="$t('search')">
          <template v-slot:prepend>
            <q-icon name="search"/>
          </template>
        </q-input>
        <avatar-button/>
      </q-toolbar>
      <q-toolbar class="xs q-pl-md q-pr-md" v-if="isShowSearch">
        <q-input :loading="searching"
                 class="full-width"
                 :debounce="500"
                 dense
                 v-model="text"
                 filled
                 clearable
                 :placeholder="$t('search')">
          <template v-slot:prepend>
            <q-icon name="search"/>
          </template>
        </q-input>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view/>
    </q-page-container>

  </q-layout>
</template>

<script>
import AvatarButton from "components/AvatarButton.vue";

export default {
  name: 'MainLayout',
  components: {AvatarButton},
  data() {
    return {
      text: "",
      searching: false,
      isShowSearch: false
    }
  },
  methods: {
    showSearch(show) {
      this.isShowSearch = show
      if (show) {
        this.text = ""
        this.onUpdate()
      }
    },
    onUpdate() {
      if (this.searching)
        return;
      this.searching = true;
      let q = this.text;
      let onDone = (result) => {
        let p = result;
        if (p instanceof Promise) {
          p.finally(() => {
            this.searching = false
            if (this.text != q)
              this.onUpdate()
          })
        } else {
          this.searching = false
        }
      }
      this.$root.$emit('onSearch', q ? q.trim() : "", onDone);

    }
  },
  beforeMount() {
    this.$root.$on("showSearch", this.showSearch)
  },
  watch: {
    '$route'() {
      this.showSearch(false)
    },
    text() {
      this.onUpdate()
    }
  }
}
</script>
