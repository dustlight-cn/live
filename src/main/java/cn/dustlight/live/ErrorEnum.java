package cn.dustlight.live;

import cn.dustlight.live.entities.ErrorBody;
import reactor.core.publisher.Mono;

public enum ErrorEnum {
    UNKNOWN_ERROR(0, "Unknown Error"),
    MISSING_PARAMETERS(1, "Missing Parameters"),

    UNAUTHORIZED(1000, "Unauthorized"),
    ACCESS_DENIED(1001, "Access Denied"),
    AUTHENTICATION_FAILURE(1002, "Authentication Failure"),

    RESOURCE_NOT_FOUND(2000, "Resource Not Found"),
    STREAM_NOT_FOUND(2001, "Stream Room Not Found"),
    USER_NOT_FOUND(2002, "User Not Found"),
    GET_STAR_FAILED(2003, "Get Star Failed"),

    CREATE_RESOURCE_FAILED(3000, "Create Resource Failed"),
    CREATE_STREAM_FAILED(3001, "Create Stream Failed"),
    CREATE_USER_FAILED(3002, "Create User Failed"),
    CREATE_STAR_FAILED(3003, "Star Failed"),

    UPDATE_RESOURCE_FAILED(4000, "Update Resource Failed"),
    UPDATE_STREAM_FAILED(4001, "Update Stream Failed"),
    UPDATE_USER_FAILED(4002, "Update User Failed"),
    UPDATE_STAR_FAILED(4003, "Update Star Failed"),

    RESOURCE_EXISTS(5000, "Resource Exists"),
    STREAM_EXISTS(5001, "Stream Exists"),
    USER_EXISTS(5002, "User Exists"),
    STAR_EXISTS(5003, "Stream Exists"),

    DELETE_RESOURCE_FAILED(6000, "Delete Resource Failed"),
    DELETE_STREAM_FAILED(6001, "Delete Stream Failed"),
    DELETE_USER_FAILED(6002, "Delete User Failed"),
    DELETE_STAR_FAILED(6003, "Cancel Star Failed");

    private int code;
    private String message, details;

    ErrorEnum(Integer code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    ErrorEnum(Integer code, String message) {
        this(code, message, null);
    }

    public ErrorEnum code(Integer code) {
        this.code = code;
        return this;
    }

    public ErrorEnum message(String message) {
        this.message = message;
        return this;
    }

    public ErrorEnum details(String details) {
        this.details = details;
        return this;
    }

    public ErrorBody getBody() {
        return new ErrorBody(code, message, details);
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public void throwException() throws ErrorBody.ServiceError {
        throw getBody().toException();
    }

    public void throwException(Throwable e) throws ErrorBody.ServiceError {
        throw getBody().toException(e);
    }

    public ErrorBody.ServiceError getException() {
        return getBody().toException();
    }

    public ErrorBody.ServiceError getException(Throwable e) {
        return getBody().toException(e);
    }

    public <T> Mono<T> getMonoError() {
        return Mono.error(getException());
    }

    public <T> Mono<T> getMonoError(Throwable e) {
        return Mono.error(getException(e));
    }
}
