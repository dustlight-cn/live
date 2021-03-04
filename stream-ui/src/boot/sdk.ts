import {UsersApi, RoomsApi, StarsApi, User} from 'src/sdk/api'
import {BASE_PATH} from 'src/sdk/base'
import {boot} from "quasar/wrappers";
import config, {Config} from "src/config";
import {LocalStorage, SessionStorage} from "quasar";
import globalAxios from 'axios';
import V from 'vue';
import {Configuration} from "src/sdk";

/**
 * Token
 */
export interface Token {
  access_token: string,
  scope: string,
  expires_in: number,
  token_type: string,
  expiredAt: number,

  isExpired(): boolean
}

/**
 * Storage
 */
export class S {
  storeInstance: SessionStorage | LocalStorage;
  bus: V;

  constructor(storeInstance: SessionStorage | LocalStorage) {
    this.storeInstance = storeInstance;
    this.bus = new V();
  }

  public storeToken(token: Token) {
    token.expiredAt = new Date().getTime() + token.expires_in * 1000;
    token.isExpired = () => new Date().getTime() > new Date(token.expiredAt).getTime();
    this.storeInstance.set("token", token);
    this.bus.$root.$emit("onTokenUpdate", token);
  }

  public loadToken(): Token | null {
    let token = <Token>this.storeInstance.getItem("token");
    if (token != null) {
      token.isExpired = () => new Date().getTime() > new Date(token.expiredAt).getTime();
    }
    if (token == null || token.isExpired()) {
      this.storeInstance.remove("user");
      this.storeInstance.remove("token");
      return null;
    }
    return token;
  }

  public onTokenUpdate(callback: (token: Token) => void) {
    this.bus.$root.$on("onTokenUpdate", callback);
  }

  public storeUser(user: User) {
    if (user != null) {
      user.nickname = user.nickname || user.username;
    }
    this.storeInstance.set("user", user);
    this.bus.$root.$emit("onUserUpdate", user);
  }

  public loadUser(): User | null {
    return this.storeInstance.getItem("user");
  }

  public onUserUpdate(callback: (user: User) => void) {
    this.bus.$root.$on("onUserUpdate", callback);
  }

  public clear() {
    this.storeInstance.remove("user")
    this.storeInstance.remove("token")
    this.bus.$root.$emit("onTokenUpdate", null);
    this.bus.$root.$emit("onUserUpdate", null);
  }
}

export class ServiceException extends Error {
  public code: number;
  public message: string;
  public details: string;

  public constructor(code: number, message: string, details: string) {
    super(details);
    this.code = code;
    this.message = message;
    this.details = details;
  }
}

declare module 'vue/types/vue' {
  interface Vue {
    $usersApi: UsersApi;
    $roomsApi: RoomsApi;
    $starsApi: StarsApi;

    $apiCfg: Configuration;
    $cfg: Config;
    $s: S;
  }
}

const errorHandler = (error: any): any => {
  let res = error == null ? null : error.response;
  let e: ServiceException;
  if (res != null && res.data != null) {
    e = new ServiceException(res.data.code, res.data.message, res.data.details);
  } else {
    e = new ServiceException(-10, error.name || "Error", error.message);
  }
  if (e.code == 1000 || e.code == 1002) {
    V.prototype.$s.clear();
  }
  if (e.code != 2001)
    V.prototype.$throw(e);
  return Promise.reject(e);
};

globalAxios.interceptors.response.use(res => res, errorHandler)

export default boot(({Vue}) => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access

  Vue.prototype.$s = new S(Vue.prototype.$q.localStorage);
  let path = config.host || BASE_PATH;
  Vue.prototype.$apiCfg = new Configuration({
    basePath: path,

    accessToken: () => {
      let s = Vue.prototype.$s;
      s.loadToken()
      let token = s.loadToken();
      if (token == null)
        return null;
      return token.access_token;
    }
  })
  Vue.prototype.$usersApi = new UsersApi(Vue.prototype.$apiCfg);
  Vue.prototype.$roomsApi = new RoomsApi(Vue.prototype.$apiCfg);
  Vue.prototype.$starsApi = new StarsApi(Vue.prototype.$apiCfg);
  Vue.prototype.$cfg = config
});




