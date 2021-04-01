<template>
  <require-authorization v-slot="{user,token}" class="column">
    {{ user != null && user.uid != null && (user_ == null || user_.uid != user.uid) ? (user_ = user, "") : "" }}
    {{ token != null && token.access_token != null && (token_ == null || token_ != token) ? (token_ = token, "") : "" }}

    <q-scroll-area ref="msg" class="q-space q-pa-xs" style="height: 100px;">
      <q-chat-message
        v-for="(m,index) in msg"
        :key="index"
        :name="m.uid == user.uid?'': m.user && (m.user.nickname && m.user.nickname.trim() || m.user.username && m.user.username.trim())?(m.user.nickname && m.user.nickname.trim()?m.user.nickname:m.user.username):m.uid"
        :text="[m.data.text]"
        :sent="m.uid==user.uid"
        name-sanitize
        text-sanitize
      >
        <template v-slot:avatar>
          <div class="text-center">
            <avatar class="q-ml-sm q-mr-sm" :user="m.user"/>
          </div>
        </template>
      </q-chat-message>
    </q-scroll-area>
    <!--    <q-separator/>-->
    <q-form v-if="token_" class="q-ma-xs">
      <q-input ref="input" dense v-model="newMsg" filled>
        <template v-slot:after>
          <q-btn
            :disable="conn == null || conn.readyState === conn.CLOSED || conn.readyState === conn.CLOSING"
            :loading="connecting"
            dense @click="send" flat color="primary" round icon="send" type="submit"/>
        </template>
      </q-input>

    </q-form>
  </require-authorization>
</template>

<script>
import RequireAuthorization from "../RequireAuthorization";
import config from "../../config";
import Avatar from "../Avatar";

export default {
  name: "Chats",
  components: {Avatar, RequireAuthorization},
  props: {
    id: {
      type: String,
      default() {
        return this.$route.path
      }
    },
    limit: {
      type: Number,
      default() {
        return 150;
      }
    },
    deleteCount: {
      type: Number,
      default() {
        return 50;
      }
    }
  },
  data() {
    return {
      conn: null,
      user_: null,
      token_: null,
      newMsg: "",
      msg: [],
      mounted: false,
      userMap: {},
      userMapFetching: {},
      connecting: false
    }
  },
  methods: {
    receiveMessage(msg) {
      try {
        if (this.msg.length >= this.limit)
          this.msg.splice(0, this.deleteCount);
        let m = JSON.parse(msg);
        if (this.userMap[m.uid] != null) {
          m.user = this.userMap[m.uid];
          this.msg.push(m)
        } else {
          if (this.userMapFetching[m.uid] == null) {
            if (this.user_ && this.user_.uid == m.uid) {
              this.userMap[m.uid] = this.user_;
              m.user = this.userMap[m.uid];
              this.msg.push(m)
            } else
              this.userMapFetching[m.uid] = this.$usersApi.getUser(m.uid)
                .then(res => {
                  this.userMap[m.uid] = res.data;
                }).finally(() => {
                  this.userMapFetching[m.uid] = null;
                })
          }
          if (this.userMapFetching[m.uid] != null) {
            this.userMapFetching[m.uid].then(() => {
              m.user = this.userMap[m.uid];
              this.msg.push(m)
            })
          }

        }
      } catch (e) {
        console.error(e)
      }

    },
    sendMessage(msg) {
      if (this.conn == null || !msg || msg.trim().length == 0)
        return
      this.conn.send(JSON.stringify({text: msg}))
    },
    send() {
      this.sendMessage(this.newMsg);
      this.newMsg = "";
      this.$refs.input.focus()
    },
    connect() {
      if (this.conn != null &&
        (this.conn.readyState == this.conn.OPEN || this.conn.readyState == this.conn.CONNECTING)) {
        if (this.conn.url == config.imHost + this.id + "?token=" + this.token_.access_token)
          return
        else {
          this.conn.onclose = null
          this.conn.close()
        }
      }
      this.connecting = true;
      let c = new WebSocket(config.imHost + this.id + "?token=" + this.token_.access_token)
      c.onmessage = (msg) => this.receiveMessage(msg.data);
      c.onopen = () => this.connecting = false;
      c.onerror = () => this.connecting = false;
      c.onclose = () => {
        this.connecting = false
        this.conn = null
        if (this.token_)
          this.connect()
      };
      this.conn = c;
    }
  },
  mounted() {
    this.mounted = true;
  },
  computed: {},
  watch: {
    token_() {
      if (this.token_ != null && this.mounted) {
        this.connect();
      }
    },
    msg() {
      this.$nextTick(() => {
        this.$refs.msg.setScrollPercentage(100)
      })
    }
  }
}
</script>

<style scoped>

</style>
