package cn.dustlight.live.controllers;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.Constants;
import cn.dustlight.live.entities.StreamHistory;
import cn.dustlight.live.services.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Tag(name = "Events", description = "事件回调")
@RequestMapping(Constants.API_ROOT)
@RestController
@CrossOrigin
public class EventController {

    private Log logger = LogFactory.getLog(getClass());

    private ReactiveOpaqueTokenIntrospector opaqueTokenIntrospector;
    private StreamService streamService;

    public EventController(ReactiveOpaqueTokenIntrospector introspector,
                           StreamService streamService) {
        this.opaqueTokenIntrospector = introspector;
        this.streamService = streamService;
    }

    @Operation(summary = "RTMP 回调", description = "用于 RTMP 推流校验，或者断开推流通知。")
    @PostMapping(value = "event/rtmp", consumes = "application/x-www-form-urlencoded")
    public Mono<Void> onRtmpEvent(RtmpEvent event) {
        String app, name, call;
        if (event != null && (app = event.getApp()) != null
                && (name = event.getName()) != null && (call = event.getCall()) != null) {
            logger.debug("RTMP Event: " + event);
            switch (call) {
                case "done":
                    return streamService.addHistory(new StreamHistory(Long.valueOf(name), StreamHistory.TYPE_DONE, null));
                case "publish":
                    String token = event.getToken();
                    if (token == null)
                        return Mono.error(ErrorEnum.UNAUTHORIZED.getException());
                    return opaqueTokenIntrospector.introspect(token)
                            .doOnNext(principal -> {
                                if (principal == null || !Objects.equals(principal.getName(), name))
                                    ErrorEnum.UNAUTHORIZED.throwException();
                                Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
                                OAuth2IntrospectionAuthenticatedPrincipal p = (OAuth2IntrospectionAuthenticatedPrincipal) principal;
                                List<String> scopes = p.getClaimAsStringList("scope");
                                if (!(authorities.contains(new SimpleGrantedAuthority("PUSH_STREAM"))
                                        && scopes.contains("push:stream")))
                                    ErrorEnum.ACCESS_DENIED.throwException();
                            })
                            .flatMap(principal ->
                                    streamService.addHistory(
                                            new StreamHistory(Long.valueOf(principal.getName()), StreamHistory.TYPE_PUSH, null))
                            );
                default:
                    return Mono.error(ErrorEnum.UNKNOWN_ERROR.getException());
            }
        } else {
            return Mono.error(ErrorEnum.MISSING_PARAMETERS.getException());
        }
    }

    @Getter
    @Setter
    @ToString
    public static class RtmpEvent implements Serializable {

        @NonNull
        private String app, name, call;
        private String token, flashver, swfurl, tcurl, pageurl, addr, clientid, type;
    }
}
