<template>
  <div>
  </div>
</template>

<script>
import qs from 'querystring'
import {v1} from 'uuid'
import {ServiceException} from "../boot/sdk";

export default {
  name: "Login",
  data() {
    return {
      response: null
    }
  },
  methods: {
    validateState(val) {
      let state = this.$q.sessionStorage.getItem('state');
      if (state == null || val == null)
        return false
      return (val == state)
    },
    generateState() {
      let uuid = v1()
      this.$q.sessionStorage.set('state', uuid);
      return uuid;
    },
    removeState() {
      this.$q.sessionStorage.remove('state')
    },
    connect() {
      let query = qs.stringify({
        scope: this.$cfg.oauth.scope,
        redirect_uri: this.$cfg.oauth.redirectUri ? location.origin + this.$cfg.oauth.redirectUri : location.href,
        client_id: this.$cfg.oauth.clientId,
        response_type: 'token',
        state: this.generateState()
      }, null, null, {})
      location.replace(this.$cfg.oauth.authorizationUri + "?" + query)
    },
    callback(response) {
      if (this.validateState(response.state)) {
        if (response.access_token && response.expires_in)
          this.onConnectSuccess(response)
        else
          this.onConnectFail(new ServiceException(0,
            response.error || "Login failed",
            response.error_description || "Token invalid"))
      } else
        this.onConnectFail(new ServiceException(0, "Login failed", "Uninitialized"))
    },
    onConnectSuccess(token) {
      this.removeState()
      this.$s.storeToken(token)
      let redirect = this.$route.query.redirect_uri
      this.$router.replace(redirect ? {path: redirect} : {name: 'index'})
    },
    onConnectFail(e) {
      throw e;
    }
  },
  mounted() {
    let response = this.$route.hash != null && this.$route.hash.trim().length > 1 ?
      qs.parse(this.$route.hash.substring(1)) : null
    if (response != null) {
      this.response = response
      this.callback(response)
    } else {
      this.connect()
    }
  }
}
</script>

<style scoped>

</style>
