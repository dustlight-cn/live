<template>
  <q-page>
    <require-authorization v-slot="{user,token}">
      {{ user != null && user.uid != null && (user_ == null || user_.uid != user.uid) ? (user_ = user, "") : "" }}
      <q-infinite-scroll ref="scroll" @load="(index,done)=>loadStars(index-1,done)">
        {{ (queryingRoom, '') }}
        <fit-grid :data="stars">
          <template v-slot="{item,index,type}">
            <q-item clickable v-ripple class="q-pa-none q-ma-sm"
                    :to="{name:'live',params:{id:item.roomId}}">
              <q-card class="full-width">
                <poster :room="starsMap[item.roomId]">
                  <div class="absolute-bottom transparent" style="padding: 4px;">
                    <q-item dense class="q-pa-none q-ma-none">
                      <q-item-section avatar style="min-width: 0px;" class="q-pa-none">
                        <avatar :user="starsMap[item.roomId]?starsMap[item.roomId].owner:null"/>
                      </q-item-section>
                      <q-item-section class="text-left q-pa-none q-pl-sm">
                        <q-item-label v-if="starsMap[item.roomId]">
                          {{ getUserDisplayName(starsMap[item.roomId].owner) }}
                        </q-item-label>
                        <q-item-label v-else>
                          <q-skeleton width="15%"/>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </div>
                </poster>
                <q-item-section class="text-left q-pa-sm">
                  <div class="text-subtitle2 ">
                    <span v-if="starsMap[item.roomId]">{{ starsMap[item.roomId].name }}</span>
                    <q-skeleton v-else width="15%"/>
                  </div>
                  <div class="text-grey row">
                    <div v-if="starsMap[item.roomId]">
                      <q-icon name="star"/>
                      {{ $util.dateFormat(item.date) }}
                    </div>
                    <q-space/>
                    <status-badge :data="starsMap[item.roomId]"/>
                  </div>
                </q-item-section>
              </q-card>

            </q-item>
          </template>
        </fit-grid>
        <template v-slot:loading>
          <div class="row justify-center q-my-md">
            <q-spinner-dots color="primary" size="40px"/>
          </div>
        </template>
      </q-infinite-scroll>
    </require-authorization>
  </q-page>
</template>

<script>
import RequireAuthorization from "../../components/RequireAuthorization";
import Poster from "../../components/Poster";
import FitGrid from "../../components/FitGrid";
import Avatar from "../../components/Avatar";
import StatusBadge from "../../components/StatusBadge";

export default {
  name: "Stars",
  components: {StatusBadge, Avatar, FitGrid, RequireAuthorization, Poster},
  data() {
    return {
      user_: null,
      loading: false,
      limit: 10,
      count: 0,
      stars: [],
      starsMap: {},
      queryingRoom: []
    }
  },
  methods: {
    loadStars(offset, done) {
      if (this.loading)
        return
      this.loading = true
      this.$starsApi.getUserStars(offset, this.limit)
        .then(res => {
          this.stars = this.stars.concat(res.data.data)
          this.count = res.data.count

          this.checkOutStars()

          if (done && (res.data.data.length == 0 || res.data.count == this.stars.length))
            done(true)
        })
        .catch(e => {
          if (done)
            done(true)
          return e
        })
        .finally(() => {
          this.loading = false
        })
    },
    checkOutStars() {
      let query = []
      this.stars.forEach(star => {
        if (this.starsMap[star.roomId] != null || this.queryingRoom.indexOf(star.roomId) > -1)
          return
        this.queryingRoom.push(star.roomId);
        query.push(star.roomId);
      })
      if (query.length == 0)
        return
      this.$roomsApi.getRooms(query)
        .then(res => {
          if (res.data.data)
            res.data.data.forEach(room => {
              this.starsMap[room.id] = room
            })
        })
        .finally(() => {
          query.forEach(id => {
            this.queryingRoom.splice(this.queryingRoom.indexOf(id), 1)
          })
        })
    },
    getUserDisplayName(user) {
      if (user == null)
        return "-"
      return user.nickname && user.nickname.trim().length > 0 ?
        user.nickname.trim() : user.username
    }
  },
  watch: {
    user_() {
      if (this.user_ != null && this.user_.uid != null) {
        this.stars = []
        this.$refs.scroll.reset()
      }
    }
  }
}
</script>

<style scoped>

</style>
