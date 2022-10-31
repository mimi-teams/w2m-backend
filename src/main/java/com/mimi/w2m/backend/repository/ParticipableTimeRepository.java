package com.mimi.w2m.backend.repository;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.ParticipableTime;
import com.mimi.w2m.backend.domain.Participant;
import com.mimi.w2m.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : teddy
 * @since : 2022/09/30
 */

public interface ParticipableTimeRepository extends JpaRepository<ParticipableTime, Long> {
    @Query("SELECT p FROM ParticipableTime p WHERE p.user = :user")
    List<ParticipableTime> findAllByUser(@Param("user") User user);
    @Query("SELECT t FROM ParticipableTime t WHERE t.event = :event")
    List<ParticipableTime> findAllByEvent(@Param("event") Event event);
    @Query("SELECT t FROM ParticipableTime t WHERE t.participant = :participant")
    List<ParticipableTime> findAllByParticipant(@Param("participant") Participant participant);
}