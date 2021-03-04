package cn.dustlight.live.services;

import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.StreamHistory;
import cn.dustlight.live.entities.StreamRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StreamService {

    Mono<StreamRoom> getRoom(Long id);

    Mono<Void> updateRoom(StreamRoom room);

    Mono<Void> deleteRoom(Long id);

    Flux<StreamRoom> getRooms(Long... ids);

    Mono<Void> addHistory(StreamHistory history);

    Mono<StreamHistory> getLatestHistory(Long id);

    Flux<StreamHistory> getLatestHistories(Long... id);

    Mono<QueryResult<StreamRoom>> searchRooms(String key, Integer offset, Integer limit);

    Mono<QueryResult<StreamRoom>> listRooms(Integer offset, Integer limit);
}
