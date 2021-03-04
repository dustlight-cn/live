<template>
  <q-page>
    <require-authorization v-slot="{user,token}">
      {{ user != null && user.uid != null && (user_ == null || user_.uid != user.uid) ? (user_ = user, "") : "" }}
      <div style="max-width: 600px;margin: 0 auto;">
        <q-card class="q-ma-md q-pa-sm">
          <div class="text-h5 q-pa-sm">
            {{ $t('menu.mine') }}
          </div>
          <q-separator/>
          <q-list>
            <q-item v-if="room">
              <q-item-section>
                <q-item-label>{{ $t('title') }}</q-item-label>
                <q-item-label caption>{{ room.name }}</q-item-label>
              </q-item-section>
            </q-item>

            <q-item v-if="room">
              <q-item-section>
                <q-item-label>{{ $t('description') }}</q-item-label>
                <q-item-label caption>{{ room.description }}</q-item-label>
              </q-item-section>
            </q-item>

            <q-item>
              <q-item-section>
                <q-item-label>{{ $t('rtmpUri') }}</q-item-label>
                <q-item-label caption>{{ getPushStreamUrl(user, token) }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>

      </div>
    </require-authorization>
  </q-page>
</template>

<script>
import RequireAuthorization from "src/components/RequireAuthorization.vue"
import {ServiceException} from "../../boot/sdk";

export default {
  name: "Mine",
  components: {RequireAuthorization},
  data() {
    return {
      user_: null,
      room: null,
      roomNotExists: false
    }
  },
  methods: {
    getStreamToken(user, token) {
      return user.uid + "?token=" + token.access_token;
    },
    getPushStreamUrl(user, token) {
      return this.$cfg.rtmpHost + this.getStreamToken(user, token);
    },
    getRoom() {
      this.$roomsApi.getRoom(this.user_.uid)
        .then(res => this.room = res.data)
        .catch(e => {
          console.log(e)
          if (e instanceof ServiceException) {
            if (e.code == 2001) {
              this.roomNotExists = true;
              return;
            }
          }
          return e;
        })
        .finally()
    }
  },
  watch: {
    user_() {
      if (this.user_ && this.user_.uid)
        this.getRoom()
    }
  }
}
</script>

<style scoped>

</style>
