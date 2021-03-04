package cn.dustlight.live.repos;

import cn.dustlight.live.entities.StreamHistory;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StreamHistoryRepository extends ReactiveCrudRepository<StreamHistory, Long> {

    @Query("SELECT * FROM " + StreamHistory.TABLE_NAME + " WHERE roomId=:roomId ORDER BY -date LIMIT 1")
    Mono<StreamHistory> getLatestHistory(@Param("roomId") Long roomId);

    @Query("SELECT a.* FROM " + StreamHistory.TABLE_NAME + " a, " +
            "(SELECT roomId,max(date) as date FROM " + StreamHistory.TABLE_NAME + " WHERE roomId IN (:roomId) GROUP BY roomId) as b " +
            "WHERE a.roomId=b.roomId AND a.date=b.date")
    Flux<StreamHistory> getLatestHistories(@Param("roomId") List<Long> roomId);

    @Modifying
    @Query("INSERT INTO " + StreamHistory.TABLE_NAME + " (roomId,type) VALUES " +
            "(:#{#history.roomId},:#{#history.type})")
    Mono<Void> add(@Param("history") StreamHistory streamHistory);
}
