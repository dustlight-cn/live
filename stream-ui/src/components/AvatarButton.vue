<template>
  <require-authorization>
    <template v-slot="{user,token}">
      <q-btn flat round>
        <avatar :user="user"/>
        <q-menu v-if="user && user.uid">
          <div class="text-center q-pa-md">
            <q-btn class="q-ma-sm" rounded dense flat :to="{name: 'mine'}">
              <avatar :size="avatarSize" :user="user"/>
            </q-btn>
            <div class="text-bold">{{ user.nickname }}</div>
            <div class="text-caption">{{ user.email }}</div>
          </div>
          <q-list style="min-width: 150px;">
            <q-separator/>
            <q-item v-ripple clickable v-close-popup
                    :to="{name: 'mine'}">
              <q-item-section class="text-center">
                {{ $t("menu.mine") }}
              </q-item-section>
            </q-item>
            <q-item v-ripple clickable v-close-popup
                    @click="openURL($cfg.oauth.userInfoUrl)">
              <q-item-section class="text-center">
                {{ $t("settings") }}
              </q-item-section>
            </q-item>
            <q-item @click="signOut" v-ripple clickable v-close-popup>
              <q-item-section class="text-center">
                {{ $t("logout") }}
              </q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-btn>
    </template>

    <template v-slot:unauthorized>
      <q-btn
        rounded
        color="primary"
        :label="$t('login')"
        replace
        no-caps
        :to="{name:'login',query: {redirect_uri: $route.fullPath}}"/>
    </template>
  </require-authorization>
</template>

<script>
import Avatar from './Avatar'
import RequireAuthorization from './RequireAuthorization';
import {openURL} from 'quasar'

export default {
  name: "AvatarButton",
  components: {
    Avatar, RequireAuthorization
  },
  props: {
    avatarSize: {
      type: Number,
      default() {
        return 50;
      }
    }
  },
  methods: {
    openURL: openURL,
    signOut() {
      this.$s.clear();
      this.$q.notify({
        message: this.$t("logoutSuccess"),
        type: 'info'
      })
    }
  }
}
</script>

<style scoped>

</style>
