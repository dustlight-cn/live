package cn.dustlight.live.controllers;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.StreamRoom;
import cn.dustlight.live.Constants;
import cn.dustlight.live.services.StreamService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Tag(name = "Rooms", description = "直播间资源。")
@RequestMapping(Constants.API_ROOT)
@RestController
@CrossOrigin
public class StreamRoomsController {

    private StreamService streamService;

    public StreamRoomsController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("rooms/{id}")
    public Mono<StreamRoom> getRoom(@PathVariable Long id) {
        return streamService.getRoom(id);
    }

    @SecurityRequirement(name = "oauth")
    @PutMapping("rooms/{id}")
    public Mono<Void> updateRoom(@PathVariable Long id,
                                 @RequestBody StreamRoom room,
                                 Authentication authentication) {
        if (id == null || room == null || !StringUtils.hasText(room.getName()))
            return ErrorEnum.MISSING_PARAMETERS.getMonoError();
        room.setName(room.getName().trim());
        if (room.getDescription() != null)
            room.setDescription(room.getDescription().trim());
        if (id.toString().equals(authentication.getName())) {
            room.setId(id);
            return streamService.updateRoom(room);
        } else
            return ErrorEnum.ACCESS_DENIED.getMonoError();
    }

    @GetMapping("rooms")
    public Mono<QueryResult<StreamRoom>> getRooms(@RequestParam(required = false, name = "id") Long[] id,
                                                  @RequestParam(required = false, name = "q") String q,
                                                  @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                  @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return id != null && id.length > 0 ?
                streamService.getRooms(id)
                        .collectList()
                        .map(streamRooms -> new QueryResult<>(streamRooms.size(), streamRooms))
                :
                StringUtils.hasText(q) ?
                        streamService.searchRooms(q, offset, limit)
                        :
                        streamService.listRooms(offset, limit);
    }
}
