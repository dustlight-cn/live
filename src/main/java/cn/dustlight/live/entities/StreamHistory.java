package cn.dustlight.live.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Table(StreamHistory.TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamHistory implements Serializable {

    public static final String TABLE_NAME = "stream_histories";
    public static final int TYPE_PUSH = 1;
    public static final int TYPE_DONE = 0;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long roomId;
    private Integer type;
    private LocalDateTime date;

}
