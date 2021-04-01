<template>
  <q-page>
    <require-authorization v-slot="{user,token}">
      {{ user != null && user.uid != null && (user_ == null || user_.uid != user.uid) ? (user_ = user, "") : "" }}
      <div style="max-width: 600px;margin: 0 auto;">
        <q-card class="q-ma-md q-pa-sm">
          <div class="text-h5 q-pa-sm row">
            {{ $t('menu.mine') }}
            <q-space/>
            <div v-if="room" class="flex-center flex">
              <status-badge :data="room"/>
            </div>
          </div>
          <q-separator class="q-mb-sm"/>
          <q-list v-if="loading.room">
            <q-item v-for="index in 4" :key="index">
              <q-item-section>
                <q-item-label>
                  <q-skeleton width="10%" type="text"/>
                </q-item-label>
                <q-item-label>
                  <q-skeleton width="30%" type="text"/>
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
          <q-list v-else>
            <div v-if="roomNotExists || !room">
              <div class="flex-center flex q-mt-md q-mb-md" style="min-height: 100px">
                <div class="text-center">
                  <div class="q-mt-md q-mb-md text-grey">
                    {{ $t('roomNotInit') }}
                  </div>
                  <q-btn :to="{name:'editRoom'}" no-caps :label="$t('create')" color="primary"/>
                </div>
              </div>
            </div>
            <div v-else>
              <q-item clickable v-ripple :to="{name:'editRoom'}">
                <q-item-section>
                  <q-item-label>{{ $t('roomTitle') }}</q-item-label>
                  <q-item-label caption>{{ room.name }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-icon name="keyboard_arrow_right"/>
                </q-item-section>
              </q-item>

              <q-item clickable v-ripple :to="{name:'editRoom'}">
                <q-item-section>
                  <q-item-label>{{ $t('roomDescription') }}</q-item-label>
                  <q-item-label caption>{{ room.description }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-icon name="keyboard_arrow_right"/>
                </q-item-section>
              </q-item>

              <q-item>
                <q-item-section>
                  <q-item-label>{{ $t('roomStars') }}</q-item-label>
                  <q-item-label caption>{{ room.stars }}</q-item-label>
                </q-item-section>
              </q-item>

              <q-separator/>

              <q-item clickable v-ripple :to="{name:'live',params:{id:room.id}}">
                <q-item-section>
                  <q-item-label>{{ $t('watch') }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-icon name="keyboard_arrow_right"/>
                </q-item-section>
              </q-item>

              <q-separator/>

              <q-expansion-item>
                <template v-slot:header>
                  <q-item-section>
                    <q-item-label>{{ $t('pushStream') }}</q-item-label>
                  </q-item-section>
                </template>
                <template v-slot:default>
                  <q-list>
                    <q-item>
                      <q-item-section>
                        <q-item-label>{{ $t('server') }}</q-item-label>
                        <q-item-label class="url" caption>
                          <span>{{ $cfg.rtmpHost }}</span>
                          <q-btn @click="()=>copyContent($cfg.rtmpHost)" size="10px" color="primary" flat round
                                 icon="content_copy"/>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section>
                        <q-item-label>{{ $t('streamToken') }}</q-item-label>
                        <q-item-label class="url" caption>
                          <span>{{ getStreamToken(user, token) }}</span>
                          <q-btn @click="()=>copyContent(getStreamToken(user, token))" size="10px" color="primary" flat
                                 round
                                 icon="content_copy"/>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section>
                        <q-item-label>{{ $t('fullStreamUrl') }}</q-item-label>
                        <q-item-label class="url" caption>
                          <span>{{ getPushStreamUrl(user, token) }}</span>
                          <q-btn @click="()=>copyContent(getPushStreamUrl(user, token))" size="10px" color="primary"
                                 flat round
                                 icon="content_copy"/>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </template>
              </q-expansion-item>
            </div>
          </q-list>
        </q-card>

      </div>
    </require-authorization>
  </q-page>
</template>

<script>
import RequireAuthorization from "src/components/RequireAuthorization.vue"
import {ServiceException} from "../../boot/sdk";
import {copyToClipboard} from 'quasar'
import StatusBadge from "../../components/StatusBadge";

export default {
  name: "Mine",
  components: {StatusBadge, RequireAuthorization},
  data() {
    return {
      user_: null,
      room: null,
      roomNotExists: false,
      loading: {
        room: false
      }
    }
  },
  methods: {
    getStreamToken(user, token) {
      return user.uid + "?token=" + token.access_token;
    },
    getPushStreamUrl(user, token) {
      return this.$cfg.rtmpHost + this.getStreamToken(user, token);
    },
    copyContent(content) {
      copyToClipboard(content)
        .then(() => {
          this.$q.notify({
            type: "positive",
            message: this.$t("copySuccess")
          })
        })
        .catch(() => {
          this.$q.notify({
            type: "negative",
            message: this.$t("copyFail")
          })
        })
    },
    getRoom() {
      if (this.loading.room)
        return
      this.loading.room = true
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
        .finally(() => this.loading.room = false)
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
.url {
  font-family: Consolas;
  word-break: break-all;
  /*font-size: 16px;*/
}
</style>
