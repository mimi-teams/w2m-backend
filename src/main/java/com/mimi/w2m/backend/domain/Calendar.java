package com.mimi.w2m.backend.domain;

import com.mimi.w2m.backend.converter.db.PlatformTypeConverter;
import com.mimi.w2m.backend.domain.type.PlatformType;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * Calendar : 외부 캘린더 정보
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
@Entity
@Getter
@Table(name = "platform_calendar", uniqueConstraints = {@UniqueConstraint(columnNames = {"platform"," event_id", "user_id"})})
public class Calendar extends BaseTimeEntity {
    @Id
    @GenericGenerator(name = "sequential_uuid", strategy = "com.mimi.w2m.backend.domain.generator.SequentialUUIDGenerator")
    @GeneratedValue(generator = "sequential_uuid")
    @Column(name = "calendar_id", columnDefinition = "BINARY(16)") //columDefinition 을 BINARY(16 으로 적어서 한참 헤맸당 ㅜㅜ
    private UUID id;

    @Comment("캘린더에 등록된 이벤트")
    @ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", updatable = false, nullable = false)
    private Event event;

    @Comment("캘린더를 등록한 이용자")
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Comment("캘린더 플랫폼")
    @Convert(converter = PlatformTypeConverter.class)
    @Column(name = "platform", nullable = false, updatable = false)
    private PlatformType platform;

    @Comment("외부 캘린더 ID")
    @Column(name = "platform_calendar_id")
    private String platformCalendarId;

    @Comment("외부 캘린더 이벤트 ID")
    @Column(name = "platform_event_id")
    private String platformEventId;

    @Builder
    public Calendar(Event event, User user, PlatformType platform, String platformCalendarId, String platformEventId) {
        this.event = event;
        this.user = user;
        this.platform = platform;
        this.platformCalendarId = platformCalendarId;
        this.platformEventId = platformEventId;
    }

    protected Calendar() {

    }
}