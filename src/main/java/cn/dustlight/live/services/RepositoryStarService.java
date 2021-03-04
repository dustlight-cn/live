package cn.dustlight.live.services;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.Star;
import cn.dustlight.live.repos.StarRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryStarService implements StarService {

    private StarRepository repository;

    @Override
    public Mono<Void> star(Long uid, Long roomId) {
        return repository
                .createStar(uid, roomId)
                .onErrorMap(throwable -> ErrorEnum.CREATE_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<Void> cancelStar(Long uid, Long roomId) {
        return repository
                .deleteStar(uid, roomId)
                .onErrorMap(throwable -> ErrorEnum.DELETE_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<Star> getStar(Long uid, Long roomId) {
        return repository
                .getStar(uid, roomId)
                .onErrorMap(throwable -> ErrorEnum.GET_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<Integer> countUserStars(Long uid) {
        return repository
                .countUserStars(uid)
                .onErrorMap(throwable -> ErrorEnum.GET_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<Integer> countRoomStars(Long roomId) {
        return repository
                .countRoomStars(roomId)
                .onErrorMap(throwable -> ErrorEnum.GET_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<QueryResult<Star>> getUserStars(Long uid, Integer offset, Integer limit) {
        return countUserStars(uid)
                .flatMap(integer -> repository.getUserStars(uid, offset, limit)
                        .collectList()
                        .map(stars -> new QueryResult<>(integer, stars))
                )
                .onErrorMap(throwable -> ErrorEnum.GET_STAR_FAILED.getException(throwable));
    }

    @Override
    public Mono<QueryResult<Star>> getRoomStars(Long roomId, Integer offset, Integer limit) {
        return countRoomStars(roomId)
                .flatMap(integer -> repository.getRoomStars(roomId, offset, limit)
                        .collectList()
                        .map(stars -> new QueryResult<>(integer, stars)))
                .onErrorMap(throwable -> ErrorEnum.GET_STAR_FAILED.getException(throwable));
    }
}
