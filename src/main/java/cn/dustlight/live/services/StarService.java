package cn.dustlight.live.services;

import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.Star;
import reactor.core.publisher.Mono;

public interface StarService {

    Mono<Void> star(Long uid, Long roomId);

    Mono<Void> cancelStar(Long uid, Long roomId);

    Mono<Star> getStar(Long uid, Long roomId);

    Mono<Integer> countUserStars(Long uid);

    Mono<Integer> countRoomStars(Long roomId);

    Mono<QueryResult<Star>> getUserStars(Long uid, Integer offset, Integer limit);

    Mono<QueryResult<Star>> getRoomStars(Long roomId, Integer offset, Integer limit);
}
