package cn.dustlight.live;

public class Constants {

    public static final String API_VERSION = "v1";

    public static final String API_ROOT = "/" + API_VERSION + "/";

    public static class OAuth {
        public static final String AUTHORIZATION_URI = "https://accounts.dustlight.cn/authorize";

        public static final String TOKEN_URI = "https://api.dustlight.cn/v1/oauth/token";

        public static final String REFRESH_TOKEN_URI = "https://api.dustlight.cn/v1/oauth/token";
    }
}
