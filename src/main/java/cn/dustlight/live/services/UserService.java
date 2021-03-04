package cn.dustlight.live.services;

import cn.dustlight.live.entities.Users;
import cn.dustlight.live.entities.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUser(Long uid);

    Mono<Users> getUsers(Long... uid);

}
