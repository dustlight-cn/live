import Vue from 'vue';
import {LocalStorage, SessionStorage} from "quasar";
import pkg from '../package.json';

export interface Config {
  /**
   * API路径
   */
  host: string,
  /**
   * 资源路径 (m3u8与截图)
   */
  resourceHost: string,
  /**
   * RTMP推流路径
   */
  rtmpHost: string,
  /**
   * 聊天室路径
   */
  imHost: string,
  /**
   * OAuth 配置
   */
  oauth: {
    /**
     * 授权地址
     */
    authorizationUri: string,
    /**
     * 回调地址
     */
    redirectUri: string | null,
    /**
     * 应用ID
     */
    clientId: string,
    /**
     * 授权作用域
     */
    scope: string,
    userInfoUrl: string
  },
  /**
   * 用户信息以及 Token 的存储方式
   */
  storage: SessionStorage | LocalStorage,
  /**
   * 获取用户信息的频率限制，单位毫秒
   */
  getUserFrequency: number,
  pkg: typeof pkg
}

const config: Config = {
  host: "https://live.api.dustlight.cn",
  imHost: "wss://api.dustlight.cn/im",
  resourceHost: "https://live-hls.dustlight.cn/live",
  rtmpHost: "rtmp://rtmp.dustlight.cn/stream/",
  oauth: {
    authorizationUri: "https://accounts.dustlight.cn/authorize",
    userInfoUrl: "https://accounts.dustlight.cn/personal-info",
    redirectUri: null,
    clientId: "7cf60210d830000",
    scope: "read:user push:stream write:stream"
  },
  storage: Vue.prototype.$q.localStorage,
  getUserFrequency: 60000, // 60秒
  pkg: pkg
}
export default config
