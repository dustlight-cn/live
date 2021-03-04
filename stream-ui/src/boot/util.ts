import {boot} from "quasar/wrappers";
import Vue from "vue";
import {ServiceException} from "./sdk";

class Util {

  public objectEquals(obj1: any, obj2: any): boolean {
    for (let k in obj1) {
      let field = obj1[k];
      if ((field instanceof Object || field instanceof Array)) {
        if ((field == null || obj2[k] == null) && field != obj2[k]) {
          return false;
        } else if (!this.objectEquals(field, obj2[k])) {
          return false;
        }
      } else {
        if (field instanceof Function)
          continue;
        if (field != obj2[k])
          return false;
      }
    }
    return true;
  }

  public dateFormat(date: Date | string, format: string) {
    if (!(date instanceof Date))
      date = new Date(date)
    let ret;
    if (format == null)
      format = "YYYY/mm/dd"
    const opt = {
      "Y+": date.getFullYear().toString(),        // 年
      "m+": (date.getMonth() + 1).toString(),     // 月
      "d+": date.getDate().toString(),            // 日
      "H+": date.getHours().toString(),           // 时
      "M+": date.getMinutes().toString(),         // 分
      "S+": date.getSeconds().toString()          // 秒
    };
    for (let k in opt) {
      ret = new RegExp("(" + k + ")").exec(format);
      if (ret) {
        // @ts-ignore
        format = format.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
      }
      ;
    }
    ;
    return format;
  }

  public handleError(err: Error, vm: Vue, info: string) {
    console.error(err);
    let msg = {
      title: "Error",
      message: "Error",
      code: -1
    }
    if (err instanceof ServiceException) {
      msg.title = err.message;
      msg.message = err.details;
      msg.code = err.code;
    } else {
      msg.title = err.name;
      msg.message = err.message;
    }
    Vue.prototype.$q.notify({
      type: 'negative',
      message: msg.title,
      caption: (msg.message ? msg.message + " " : "") + (msg.code > 0 ? "(Error Code: " + msg.code + ")" : "")
    })
  }
}

declare module 'vue/types/vue' {
  interface Vue {
    $util: Util;
  }
}

const util = new Util();

export default boot(({Vue}) => {
  Vue.prototype.$util = util;
  Vue.config.errorHandler = util.handleError;
  Vue.prototype.$throw = util.handleError;
})
