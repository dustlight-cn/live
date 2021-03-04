package cn.dustlight.live.repos;

import cn.dustlight.live.entities.Star;
import cn.dustlight.live.entities.StreamHistory;
import cn.dustlight.live.entities.StreamRoom;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StreamRoomRepository extends ReactiveCrudRepository<StreamRoom, Long> {

    @Modifying
    @Query("INSERT INTO " + StreamRoom.TABLE_NAME + " (`id`,`name`,`description`) VALUES " +
            "(:#{#room.id},:#{#room.name},:#{#room.description}) ON DUPLICATE KEY " +
            "UPDATE description=:#{#room.description},name=:#{#room.name}")
    Mono<Void> insertOrUpdate(@Param("room") StreamRoom room);

    @Query("SELECT r.*,count(s.uid) AS stars " +
            "FROM (SELECT * FROM " + StreamRoom.TABLE_NAME + " WHERE id=:id LIMIT 1) AS r " +
            "LEFT JOIN " + Star.TABLE_NAME + " AS s " +
            "ON s.roomId=r.id " +
            "GROUP BY r.id")
    Mono<StreamRoom> getRoom(@Param("id") Long id);

    @Query("SELECT r.*,count(s.uid) AS stars " +
            "FROM (SELECT * FROM " + StreamRoom.TABLE_NAME + " WHERE id IN (:id)) AS r " +
            "LEFT JOIN " + Star.TABLE_NAME + " AS s " +
            "ON r.id=s.roomId " +
            "GROUP BY r.id")
    Flux<StreamRoom> getRooms(@Param("id") List<Long> id);

    @Query("SELECT r.*,count(s.uid) AS stars " +
            "FROM (SELECT * FROM " + StreamRoom.TABLE_NAME + " WHERE MATCH(name,description) AGAINST(:key) LIMIT :offset,:limit) AS r " +
            "LEFT JOIN " + Star.TABLE_NAME + " AS s " +
            "ON r.id=s.roomId " +
            "GROUP BY r.id ORDER BY -stars ")
    Flux<StreamRoom> searchRooms(@Param("key") String key,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    @Query("SELECT COUNT(id) FROM " + StreamRoom.TABLE_NAME + " WHERE MATCH(name,description) AGAINST(:key)")
    Mono<Integer> countSearchRooms(@Param("key") String key);

    @Query("SELECT b.*,a.type AS type,COUNT(s.uid) AS stars FROM ( " +
            "SELECT b.* " +
            "FROM (SELECT roomId,MAX(date) AS date FROM " + StreamHistory.TABLE_NAME + " GROUP BY roomId) AS a " +
            "LEFT JOIN " + StreamHistory.TABLE_NAME + " AS b " +
            "ON a.roomId=b.roomId AND a.date=b.date " +
            ") AS a " +
            "RIGHT JOIN " + StreamRoom.TABLE_NAME + " AS b " +
            "ON a.roomId=b.id " +
            "LEFT JOIN " + Star.TABLE_NAME + " AS s " +
            "ON s.roomId=b.id " +
            "GROUP BY b.id " +
            "ORDER BY type is NULL,type DESC,stars DESC LIMIT :offset,:limit")
    Flux<StreamRoom> listRooms(@Param("offset") Integer offset,
                               @Param("limit") Integer limit);
}
