package cn.dustlight.live.controllers;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.services.StarService;
import cn.dustlight.live.Constants;
import cn.dustlight.live.entities.QueryResult;
import cn.dustlight.live.entities.Star;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RequestMapping(Constants.API_ROOT)
@RestController
@Tag(name = "Stars", description = "关注资源。")
@CrossOrigin
public class StarsController {

    private Log logger = LogFactory.getLog(getClass());

    private StarService starService;

    public StarsController(StarService starService) {
        this.starService = starService;
    }

    @SecurityRequirement(name = "oauth")
    @GetMapping("user/star")
    public Mono<Star> getStar(@RequestParam("rid") Long rid,
                              Principal principal) {
        if (principal.getName() == null || !StringUtils.hasText(principal.getName()))
            ErrorEnum.UNAUTHORIZED.getMonoError();
        return starService.getStar(Long.valueOf(principal.getName()), rid);
    }

    @SecurityRequirement(name = "oauth")
    @PostMapping("user/star")
    public Mono<Void> createStar(@RequestParam("rid") Long rid,
                                 Principal principal) {
        if (principal.getName() == null || !StringUtils.hasText(principal.getName()))
            ErrorEnum.UNAUTHORIZED.getMonoError();
        return starService.star(Long.valueOf(principal.getName()), rid);
    }

    @SecurityRequirement(name = "oauth")
    @DeleteMapping("user/star")
    public Mono<Void> deleteStar(@RequestParam("rid") Long rid,
                                 Principal principal) {
        if (principal.getName() == null || !StringUtils.hasText(principal.getName()))
            ErrorEnum.UNAUTHORIZED.getMonoError();
        return starService.cancelStar(Long.valueOf(principal.getName()), rid);
    }

    @SecurityRequirement(name = "oauth")
    @GetMapping("user/stars-count")
    public Mono<Integer> countUserStars(Principal principal) {
        if (principal.getName() == null || !StringUtils.hasText(principal.getName()))
            ErrorEnum.UNAUTHORIZED.getMonoError();
        return starService.countUserStars(Long.valueOf(principal.getName()));
    }

    @GetMapping("rooms/{rid}/stars-count")
    public Mono<Integer> countRoomStars(@PathVariable("rid") Long rid) {
        return starService.countRoomStars(rid);
    }

    @SecurityRequirement(name = "oauth")
    @GetMapping("users/stars")
    public Mono<QueryResult<Star>> getUserStars(Principal principal,
                                                @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        if (principal.getName() == null || !StringUtils.hasText(principal.getName()))
            ErrorEnum.UNAUTHORIZED.getMonoError();
        return starService.getUserStars(Long.valueOf(principal.getName()), offset, limit);
    }

    @GetMapping("rooms/{rid}/stars")
    public Mono<QueryResult<Star>> getRoomStars(@PathVariable("rid") Long rid,
                                                @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return starService.getRoomStars(rid, offset, limit);
    }
}
