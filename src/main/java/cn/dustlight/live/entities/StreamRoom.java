package cn.dustlight.live.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Table(StreamRoom.TABLE_NAME)
public class StreamRoom implements Serializable {

    public static final String TABLE_NAME = "rooms";

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String description;

    @ReadOnlyProperty
    private LocalDateTime createdAt;
    @ReadOnlyProperty
    private LocalDateTime updatedAt;
    @ReadOnlyProperty
    private User owner;
    @ReadOnlyProperty
    private Integer stars;
    @ReadOnlyProperty
    private boolean streaming;
}
