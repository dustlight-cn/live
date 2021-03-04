<template>
  <q-page>
    <div class="q-pa-sm row justify-center" v-if="theRoom && theRoom.streaming">
      <q-card style="width: 599px;">
        <q-item class="q-pa-none" clickable v-ripple :to="{name:'live',params:{id:theRoom.id}}">
          <q-item-section>
            <q-item>
              <q-item-section avatar>
                <avatar :size="40" :user="theRoom.owner"/>
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-subtitle1 text-bold">{{ theRoom.name }}</q-item-label>
                <q-item-label caption>{{ getUserDisplayName(theRoom.owner) }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <status-badge :data="theRoom"/>
              </q-item-section>
            </q-item>
            <stream-player :room="theRoom"/>
            <q-item>
              <q-item-section>
                <q-item-label caption>{{ theRoom.description }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-item-label>
                  <q-icon name="person"/>
                  {{ theRoom.stars }}
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-item-section>
        </q-item>
      </q-card>
    </div>
    <fit-grid v-if="!loading" :data="rooms">
      <template v-slot="{item,index,type}">
        <q-item clickable v-ripple class="q-pa-none q-ma-sm"
                :to="{name:'live',params:{id:item.id}}">
          <q-card class="full-width">
            <poster :room="item">
              <div class="absolute-bottom transparent" style="padding: 4px;">
                <q-item dense class="q-pa-none q-ma-none">
                  <q-item-section avatar style="min-width: 0px;" class="q-pa-none">
                    <avatar :user="item.owner"/>
                  </q-item-section>
                  <q-item-section class="text-left q-pa-none q-pl-sm">
                    <q-item-label>
                      {{ getUserDisplayName(item.owner) }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </div>
            </poster>
            <q-item-section class="text-left q-pa-sm">
              <div class="text-subtitle2 ">
                {{ item.name }}
              </div>
              <div class="text-grey row">
                <div>
                  <q-icon name="star"/>
                  {{ item.stars }}
                </div>
                <q-space/>
                <status-badge :data="item"/>
              </div>
            </q-item-section>
          </q-card>
        </q-item>
      </template>
    </fit-grid>
    <q-inner-loading :showing="loading" size="50">
      <q-spinner-dots size="50" color="primary"/>
    </q-inner-loading>
    <div v-if="count>0 && Math.ceil(count/limit) > 1" class="q-pa-lg flex flex-center">
      <q-pagination
        :disable="loading"
        v-model="page"
        :max="Math.ceil(count/limit)"
        :input="true"
      >
      </q-pagination>
    </div>
  </q-page>

</template>

<script>
import Avatar from "components/Avatar.vue";
import Poster from "components/Poster.vue";
import FitGrid from "components/FitGrid.vue";
import StatusBadge from "../../components/StatusBadge";
import StreamPlayer from "../../components/StreamPlayer";

export default {
  name: "Home",
  components: {StreamPlayer, StatusBadge, FitGrid, Avatar, Poster},
  data() {
    return {
      rooms: [],
      theRoom: null,
      count: 0,
      loading: false,
      page: 1,
      limit: 10,
    }
  },
  methods: {
    refresh() {
      this.page = 1
      this.loadStreamRooms();
    },
    loadStreamRooms() {
      if (this.loading)
        return
      this.loading = true
      this.$roomsApi.getRooms(null, "", (this.page - 1) * this.limit, this.limit)
        .then(res => {
          this.rooms = res.data.data
          if (this.rooms != null && this.rooms[0] != null && this.rooms[0].streaming == true
            && (this.theRoom == null || this.theRoom.id == this.rooms[0].id)) {
            this.theRoom = this.rooms[0];
            this.rooms = this.rooms.slice(1);
          }
          this.count = res.data.count
        })
        .finally(() => this.loading = false)
    },
    getUserDisplayName(user) {
      if (user == null)
        return "-"
      return user.nickname && user.nickname.trim().length > 0 ?
        user.nickname.trim() : user.username
    }
  },
  mounted() {
    this.refresh();
  },
  watch: {
    page() {
      this.loadStreamRooms()
    }
  }
}
</script>

<style scoped>

</style>
