package cn.dustlight.live.repos;

import cn.dustlight.live.entities.Star;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StarRepository extends Repository<Star,Long> {

    @Query("SELECT * FROM " + Star.TABLE_NAME + " WHERE uid=:uid AND roomId=:roomId LIMIT 1")
    Mono<Star> getStar(@Param("uid") Long uid,
                                @Param("roomId") Long roomId);

    @Modifying
    @Query("INSERT INTO " + Star.TABLE_NAME + " SET uid=:uid ,roomId=:roomId" )
    Mono<Void> createStar(@Param("uid") Long uid,
                          @Param("roomId") Long roomId);

    @Modifying
    @Query("DELETE FROM " + Star.TABLE_NAME + " WHERE uid=:uid AND roomId=:roomId LIMIT 1" )
    Mono<Void> deleteStar(@Param("uid") Long uid,
                          @Param("roomId") Long roomId);

    @Query("SELECT COUNT(uid) FROM " + Star.TABLE_NAME + " WHERE roomId=:roomId")
    Mono<Integer> countRoomStars(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(roomId) FROM " + Star.TABLE_NAME + " WHERE uid=:uid")
    Mono<Integer> countUserStars(@Param("uid") Long uid);

    @Query("SELECT uid,date FROM " + Star.TABLE_NAME + " WHERE roomId=:roomId LIMIT :offset,:limit")
    Flux<Star> getRoomStars(@Param("roomId") Long roomId,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    @Query("SELECT roomId,date FROM " + Star.TABLE_NAME + " WHERE uid=:uid LIMIT :offset,:limit")
    Flux<Star> getUserStars(@Param("uid") Long uid,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
}
