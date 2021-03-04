package cn.dustlight.live.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorBody implements Serializable {

    private Integer code;

    private String message;

    private String details;

    public ErrorBody(Integer code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public ErrorBody(Integer code, String message) {
        this(code, message, null);
    }

    public ServiceError toException() {
        return new ServiceError(this);
    }

    public ServiceError toException(Throwable e) {
        return new ServiceError(this, e);
    }

    @Getter
    public static class ServiceError extends RuntimeException {

        private ErrorBody errorBody;

        public ServiceError(ErrorBody errorBody) {
            super(errorBody.getMessage() + (errorBody.getDetails() == null ?
                    "" : "; " + errorBody.getDetails()));
            this.errorBody = errorBody;
        }

        public ServiceError(ErrorBody errorBody, Throwable e) {
            super((errorBody.getMessage() + (errorBody.getDetails() == null ?
                    "" : "; " + errorBody.getDetails()))
                    + "; " + e.getMessage(), e);
            this.errorBody = errorBody;
        }
    }
}
