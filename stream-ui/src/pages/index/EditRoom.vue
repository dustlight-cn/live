<template>
  <q-page>
    <require-authorization v-slot="{user,token}">
      {{ user != null && user.uid != null && (user_ == null || user_.uid != user.uid) ? (user_ = user, "") : "" }}
      <div style="max-width: 600px;margin: 0 auto;">
        <q-card class="q-ma-md q-pa-sm">
          <div class="text-h5 q-pa-sm row">
            <q-btn dense @click="$router.back()" class="q-mr-sm" flat round icon="arrow_back"/>
            <q-separator vertical/>
            <div class="flex flex-center q-pl-md">{{ $t('editRoom') }}</div>
          </div>
          <q-separator/>
          <q-form @submit="update">
            <q-card-section class="q-gutter-md">
              <q-input :rules="rule" v-model="model.title" :disable="loading" filled :label="$t('roomTitle')"/>

              <q-input :rules="rule" v-model="model.description" :disable="loading" type="textarea" filled
                       :label="$t('roomDescription')"/>
            </q-card-section>
            <div align="right" class="q-pa-md">
              <q-btn :disable="user_==null" type="submit" :loading="loading" :label="$t(roomNotExists?'create':'save')"
                     color="primary"/>
            </div>
          </q-form>
        </q-card>
      </div>
    </require-authorization>
  </q-page>
</template>

<script>
import RequireAuthorization from "../../components/RequireAuthorization";
import {ServiceException} from "../../boot/sdk";

export default {
  name: "EditRoom",
  components: {RequireAuthorization},
  data() {
    return {
      user_: null,
      room: null,
      roomNotExists: false,
      model: {
        title: "",
        description: ""
      },
      rule: [val => val != null && val.trim().length > 0],
      loading: false
    }
  },
  methods: {
    getRoom() {
      if (this.loading)
        return
      this.loading = true
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
        .finally(() => this.loading = false)
    },
    update() {
      if (this.loading)
        return
      this.loading = true
      this.$roomsApi.updateRoom(this.user_.uid,
        {
          name: this.model.title,
          description: this.model.description
        })
        .then(this.onSuccess)
        .finally(() => this.loading = false)
    },
    onSuccess(res) {
      this.$q.notify({
        type: "positive",
        message: this.roomNotExists ? this.$t('createSuccess') : this.$t('updateSuccess')
      })
      this.$router.back();
    }
  },
  watch: {
    user_() {
      if (this.user_ && this.user_.uid)
        this.getRoom()
    },
    room() {
      this.model.title = this.room.name
      this.model.description = this.room.description
    }
  }
}
</script>

<style scoped>

</style>
