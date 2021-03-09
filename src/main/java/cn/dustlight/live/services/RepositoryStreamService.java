package cn.dustlight.live.services;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.StreamHistory;
import cn.dustlight.live.entities.StreamRoom;
import cn.dustlight.live.repos.StreamHistoryRepository;
import cn.dustlight.live.entities.User;
import cn.dustlight.live.repos.StreamRoomRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryStreamService implements StreamService {

    private StreamRoomRepository repository;
    private UserService userService;
    private StreamHistoryRepository historyRepository;
    private StarService starService;

    @Override
    public Mono<StreamRoom> getRoom(Long id) {
        return repository
                .getRoom(id)
                .filterWhen(streamRoom -> userService
                        .getUser(streamRoom.getId())
                        .doOnNext(user -> streamRoom.setOwner(user))
                        .onErrorMap(throwable -> ErrorEnum.STREAM_NOT_FOUND.getException(throwable))
                        .then(Mono.just(streamRoom.getOwner() != null))
                )
                .filterWhen(room -> getLatestHistory(room.getId())
                        .doOnNext(streamHistory -> room.setStreaming(streamHistory.getType() == StreamHistory.TYPE_PUSH))
                        .then(Mono.just(true))
                )
                .switchIfEmpty(Mono.error(ErrorEnum.STREAM_NOT_FOUND.getException()));
    }

    @Override
    public Mono<Void> updateRoom(StreamRoom streamRoom) {
        if (streamRoom == null)
            return Mono.error(ErrorEnum.UPDATE_STREAM_FAILED.getException());
        return repository
                .insertOrUpdate(streamRoom)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_STREAM_FAILED.getException(throwable));
    }

    @Override
    public Mono<Void> deleteRoom(Long id) {
        return repository
                .deleteById(id)
                .onErrorMap(throwable -> ErrorEnum.DELETE_STREAM_FAILED.getException(throwable));
    }

    @Override
    public Flux<StreamRoom> getRooms(Long... ids) {
        return repository
                .getRooms(Arrays.asList(ids))
                .collectList()
                .map(RepositoryStreamService::toRoomMap)
                .filterWhen(map -> userService
                        .getUsers(ids)
                        .map(users -> users.getData())
                        .map(users -> {
                            for (User user : users)
                                if (map.containsKey(user.getUid()))
                                    map.get(user.getUid()).setOwner(user);
                            return true;
                        }))
                .filterWhen(map -> getLatestHistories(ids)
                        .collectList()
                        .map(streamHistories -> {
                            for (StreamHistory history : streamHistories)
                                if (map.containsKey(history.getRoomId()))
                                    map.get(history.getRoomId()).setStreaming(history.getType() == StreamHistory.TYPE_PUSH);
                            return true;
                        }))
                .flux()
                .flatMap(map -> Flux.fromIterable(map.values()))
                ;
    }

    @Override
    public Mono<Void> addHistory(StreamHistory history) {
        return historyRepository
                .add(history)
                .onErrorMap(throwable -> ErrorEnum.UPDATE_STREAM_FAILED.getException(throwable));
    }

    @Override
    public Mono<StreamHistory> getLatestHistory(Long id) {
        return historyRepository.getLatestHistory(id);
    }

    @Override
    public Flux<StreamHistory> getLatestHistories(Long... id) {
        return historyRepository.getLatestHistories(Arrays.asList(id));
    }

    @Override
    public Mono<QueryResult<StreamRoom>> searchRooms(String key, Integer offset, Integer limit) {
        return repository
                .countSearchRooms(key)
                .flatMap(count -> fillStreamRoomEntities(repository.searchRooms(key, offset, limit), count));
    }

    @Override
    public Mono<QueryResult<StreamRoom>> listRooms(Integer offset, Integer limit) {
        return repository
                .count()
                .map(v -> v.intValue())
                .flatMap(count -> fillStreamRoomEntities(repository.listRooms(offset, limit), count));
    }

    public static Map<Long, StreamRoom> toRoomMap(Collection<StreamRoom> rooms) {
        LinkedHashMap<Long, StreamRoom> map = new LinkedHashMap<>();
        for (StreamRoom room : rooms) {
            map.put(room.getId(), room);
        }
        return map;
    }

    public static Long[] getRoomIds(Collection<StreamRoom> rooms) {
        Long[] c = new Long[rooms.size()];
        int i = 0;
        for (StreamRoom room : rooms)
            c[i++] = room.getId();
        return c;
    }

    protected Mono<QueryResult<StreamRoom>> fillStreamRoomEntities(Flux<StreamRoom> roomFlux, Integer count) {
        return count > 0 ? roomFlux.collectList()
                .map(RepositoryStreamService::toRoomMap)
                .filterWhen(map -> map.size() > 0 ? historyRepository
                        .getLatestHistories(Arrays.asList(getRoomIds(map.values())))
                        .collectList()
                        .map(streamHistories -> {
                            for (StreamHistory history : streamHistories)
                                if (map.containsKey(history.getRoomId()))
                                    map.get(history.getRoomId()).setStreaming(history.getType() == StreamHistory.TYPE_PUSH);
                            return true;
                        }) :
                        Mono.just(true)
                )
                .filterWhen(map -> map.size() > 0 ? userService
                        .getUsers(getRoomIds(map.values()))
                        .map(users -> users.getData())
                        .map(users -> {
                            for (User user : users)
                                if (map.containsKey(user.getUid()))
                                    map.get(user.getUid()).setOwner(user);
                            return true;
                        }) :
                        Mono.just(true)
                )
                .map(map -> map.values())
                .map(streamRooms -> new QueryResult<>(count, streamRooms))
                :
                Mono.just(new QueryResult<>(0, Collections.emptyList()));
    }
}
