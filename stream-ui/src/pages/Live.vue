<template>
  <div>
    <q-page class="container" v-if="flag">
      <stream-player :room="room"/>
      <q-list>
        <q-expansion-item
          default-opened>
          <template v-slot:header>
            <q-item-section>
              <q-item-label class="text-h6">
                <q-skeleton v-if="loading || !room" width="35%" type="text"/>
                <span v-else>{{ room.name || "-" }}</span>
              </q-item-label>
            </q-item-section>
            <q-item-section side>
              <status-badge :data="room"/>
            </q-item-section>
          </template>
          <template v-slot:default>
            <q-separator/>
            <q-card>
              <q-card-section>
                <q-skeleton v-if="loading || !room" width="35%" type="text"/>
                <span v-else>{{ room.description || "-" }}</span>
              </q-card-section>
            </q-card>
          </template>
        </q-expansion-item>
        <q-separator/>
        <q-item>
          <q-item-section avatar style="min-width: 0px;" class="q-pa-none">
            <avatar :user="owner"/>
          </q-item-section>
          <q-item-section class="q-pl-sm">
            <q-item-label>
              <q-skeleton v-if="loading || !owner" width="15%" type="text"/>
              <span v-else>{{ ownerName || "-" }}</span>
            </q-item-label>
            <q-item-label caption>
              <q-skeleton v-if="loading || !room" width="10%" type="text"/>
              <span v-else><q-icon name="star"/> {{ room.stars || "0" }}</span>
            </q-item-label>
          </q-item-section>
          <q-item-section v-if="room" side>
            <q-btn no-caps :color="star?'grey':'pink'" flat
                   @click="onStarButtonClick"
                   :loading="starLoading"
                   :disable="starLoading"
                   :label="starButtonText"/>
          </q-item-section>
        </q-item>
        <q-separator/>
      </q-list>
      <chat-box/>
    </q-page>

    <q-page v-else>
      <div class="q-pt-lg q-pl-xs q-pr-xs q-pb-lg">
        <div class="fit row q-gutter-sm justify-center">
          <q-card style="width: calc(100% - 250px);max-width: 906px">
            <q-item>
              <q-item-section avatar>
                <avatar :user="owner" :size="45"/>
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-subtitle1 text-bold">
                  <q-skeleton v-if="loading || !room" width="35%" type="text"/>
                  <span v-else>{{ room.name || "-" }}</span>
                </q-item-label>
                <q-item-label caption>
                  <q-skeleton v-if="loading || !owner" width="15%" type="text"/>
                  <span v-else>{{ ownerName || "-" }}</span>
                </q-item-label>
              </q-item-section>
              <q-item-section side class="text-subtitle2">
                <status-badge :data="room"/>
              </q-item-section>
            </q-item>
            <stream-player :room="room"/>
            <q-item>
              <q-item-section>
                <q-item-label caption>
                  <q-skeleton v-if="loading || !room" width="25%" type="text"/>
                  <span v-else>{{ room.description || "-" }}</span>
                </q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-skeleton v-if="loading || !room" width="20px" type="text"/>
                <span v-else><q-icon name="star"/> {{ room.stars || "0" }}</span>
              </q-item-section>
              <q-item-section v-if="room" side>
                <q-btn no-caps :color="star?'grey':'pink'" flat
                       @click="onStarButtonClick"
                       :loading="starLoading"
                       :disable="starLoading"
                       :label="starButtonText"/>
              </q-item-section>
            </q-item>
          </q-card>

          <q-card style="width: 200px">
            <chat-box class="full-height q-pt-sm"/>
          </q-card>

        </div>
      </div>
    </q-page>

  </div>
</template>

<script>
import RequireAuthorization from "components/RequireAuthorization.vue";
import Avatar from "components/Avatar.vue";
import StatusBadge from "../components/StatusBadge";
import StreamPlayer from "../components/StreamPlayer";
import ChatBox from "../components/chat/ChatBox";

export default {
  name: "Live",
  components: {ChatBox, StreamPlayer, StatusBadge, Avatar, RequireAuthorization},
  data() {
    return {
      id: this.$route.params.id,
      room: null,
      loading: true,
      starLoading: false,
      user: null,
      star: null,
      flag: this.$q.screen.lt.sm
    }
  },
  methods: {
    getRoom(id) {
      this.loading = true
      this.$roomsApi.getRoom(id)
        .then(res => this.room = res.data)
        .finally(() => this.loading = false)
    },
    onUserUpdate(user) {
      this.user = user
    },
    getStar() {
      this.starLoading = true
      this.$starsApi.getStar(this.id)
        .then(res => this.star = res.data)
        .finally(() => this.starLoading = false)
    },
    onStarButtonClick() {
      if (this.user && this.user.uid) {
        this.starLoading = true;
        let p = this.star ?
          this.$starsApi.deleteStar(this.id).then(() => {
            this.star = null
            this.room.stars -= 1
          }) :
          this.$starsApi.createStar(this.id).then(() => {
            this.star = {
              roomId: this.id,
              uid: this.user.uid,
              data: new Date().getTime()
            }
            this.room.stars += 1
          })
        p.finally(() => this.starLoading = false)
      } else {
        this.$router.push({name: 'login', query: {redirect_uri: this.$route.fullPath}})
      }
    }
  },
  computed: {
    owner() {
      return this.room == null ? null : this.room.owner;
    },
    ownerName() {
      if (this.owner == null)
        return "-";
      let n = this.owner.nickname == null || this.owner.nickname.trim().length == 0 ?
        this.owner.username : this.owner.nickname;
      return n ? n.trim() : "-";
    },
    starButtonText() {
      return this.$t(this.star ? "subscribed" : "subscribe");
    }
  },
  mounted() {
    this.getRoom(this.id)
    this.onUserUpdate(this.$s.loadUser())
    this.$s.onUserUpdate(this.onUserUpdate)
  },
  watch: {
    user() {
      if (this.user != null && this.user.uid != null) {
        this.getStar()
      } else {
        this.star = null
      }
    }
  }
}
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
}

.container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.container > div:last-child {
  flex: 1;
}
</style>
