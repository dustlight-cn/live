package cn.dustlight.live.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;
    private String username;
    private String nickname;
    private String avatar;

}
