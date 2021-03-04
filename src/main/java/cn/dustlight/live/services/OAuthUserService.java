package cn.dustlight.live.services;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.entities.Users;
import cn.dustlight.live.entities.User;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OAuthUserService implements UserService {

    private static final String DEFAULT_BASE_URI = "https://api.dustlight.cn/v1/";
    private static final String GET_USER_URI = "users/{uid}";
    private static final String GET_USERS_URI = "users?uid={uid}";

    private WebClient webClient;
    private String baseUri;

    public OAuthUserService(WebClient webClient, String baseUri) {
        this.webClient = webClient;
        this.baseUri = baseUri != null ? baseUri : DEFAULT_BASE_URI;
    }

    public OAuthUserService(WebClient webClient) {
        this(webClient, null);
    }

    @Override
    public Mono<User> getUser(Long uid) {
        return webClient
                .get()
                .uri(baseUri + GET_USER_URI, uid)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorMap(throwable -> ErrorEnum.USER_NOT_FOUND.getException(throwable))
                .switchIfEmpty(Mono.error(ErrorEnum.USER_NOT_FOUND.getException()));
    }

    @Override
    public Mono<Users> getUsers(Long... uid) {
        StringBuilder uidString = new StringBuilder();
        if (uid != null) {
            for (Long u : uid) {
                if (uidString.length() > 0)
                    uidString.append(',');
                uidString.append(u);
            }
        }
        return webClient
                .get()
                .uri(baseUri + GET_USERS_URI, uidString.toString())
                .retrieve().bodyToMono(Users.class)
                .onErrorMap(throwable -> ErrorEnum.UNAUTHORIZED.getException(throwable));
    }
}
