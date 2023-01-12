package com.mimi.w2m.backend.repository;

import com.mimi.w2m.backend.domain.Calendar;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.domain.type.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CalendarRepository
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/11
 **/
public interface CalendarRepository extends JpaRepository<Calendar, UUID> {
    @Query(value = "select calendar from Calendar calendar where calendar.user = :user and calendar.event = :event and calendar.platform = :platform")
    Optional<Calendar> findByUserAndEventInPlatform(@Param("user") User user, @Param("event") Event event, @Param("platform") PlatformType platform);

    @Query(value = "select calendar from Calendar calendar where calendar.user = :user and calendar.platform = :platform and calendar.platformCalendarId = :calendarId")
    List<Calendar> findAllByUserInPlatformCalendar(@Param("user") User user, @Param("platform") PlatformType platform, @Param("calendarId") String calendarId);

    @Modifying
    @Query(value = "delete from Calendar calendar where calendar.event = :event")
    void deleteByEvent(@Param("event") Event event);

    @Modifying
    @Query(value = "delete from Calendar  calendar where calendar.user = :user")
    void deleteByUser(@Param("user") User user);
}
