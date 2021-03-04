package cn.dustlight.live.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Table(Star.TABLE_NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Star implements Serializable {

    public static final String TABLE_NAME = "stars";

    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid, roomId;
    private LocalDateTime date;
}
