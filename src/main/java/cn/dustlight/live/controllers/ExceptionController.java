package cn.dustlight.live.controllers;

import cn.dustlight.live.ErrorEnum;
import cn.dustlight.live.entities.ErrorBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsProcessor;
import org.springframework.web.cors.reactive.DefaultCorsProcessor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@CrossOrigin
public class ExceptionController {

    private Log logger = LogFactory.getLog(getClass());
    private ObjectMapper mapper;

    private static CorsConfiguration corsConfiguration = new CorsConfiguration();

    static {
        corsConfiguration.addAllowedOrigin("*");
    }

    @Getter
    @Setter
    private CorsProcessor corsProcessor = new DefaultCorsProcessor();

    public ExceptionController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ErrorBody> onException(Throwable e, ServerWebExchange webExchange) {
        webExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error(e.getMessage(), e);
        return logger.isDebugEnabled() ?
                Mono.just(ErrorEnum.UNKNOWN_ERROR.details(e.getMessage()).getBody()) :
                Mono.just(ErrorEnum.UNKNOWN_ERROR.getBody());
    }

    @ExceptionHandler(ErrorBody.ServiceError.class)
    public Mono<ErrorBody> onServiceError(ErrorBody.ServiceError e, ServerWebExchange webExchange) {
        ErrorBody errorBody = e.getErrorBody();
        int code = errorBody.getCode();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (code == ErrorEnum.UNAUTHORIZED.getCode())
            status = HttpStatus.UNAUTHORIZED;
        else if (code == ErrorEnum.ACCESS_DENIED.getCode())
            status = HttpStatus.FORBIDDEN;
        else if (code >= ErrorEnum.RESOURCE_NOT_FOUND.getCode()
                && code < ErrorEnum.RESOURCE_NOT_FOUND.getCode() + 1000)
            status = HttpStatus.NOT_FOUND;
        else if (code >= ErrorEnum.RESOURCE_EXISTS.getCode()
                && code < ErrorEnum.RESOURCE_EXISTS.getCode() + 1000)
            status = HttpStatus.CONFLICT;
        webExchange.getResponse().setStatusCode(status);
        logger.warn(e.getMessage(), e);
        if (logger.isDebugEnabled() && (errorBody.getDetails() == null || errorBody.getDetails().isEmpty())
                && !errorBody.getMessage().equals(e.getMessage()))
            errorBody.setDetails(e.getMessage());
        return Mono.just(errorBody);
    }

    public Mono<Void> handlerException(ServerWebExchange exchange, Throwable e) {
        Mono<ErrorBody> bodyMono = null;
        if (e instanceof AccessDeniedException)
            bodyMono = onServiceError(ErrorEnum.ACCESS_DENIED.getException(e), exchange);
        else if (e instanceof AuthenticationException)
            bodyMono = onServiceError(ErrorEnum.UNAUTHORIZED.getException(e), exchange);
        else
            bodyMono = onException(e, exchange);
        corsProcessor.process(corsConfiguration, exchange);
        return exchange.getResponse().writeWith(bodyMono.map(errorBody -> {
            NettyDataBufferFactory factory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            byte[] bytes = new byte[0];
            try {
                bytes = mapper.writeValueAsBytes(errorBody);
            } catch (JsonProcessingException jsonProcessingException) {
                logger.error(jsonProcessingException);
            }
            return factory.wrap(bytes);
        }));
    }

}
