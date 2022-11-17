package com.mimi.w2m.backend.repository;

import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * EventRepository
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/17
 **/

public interface EventRepository extends JpaRepository<Event, Long> {
Optional<Event> findByTitle(String title);

@Query("SELECT e FROM Event e WHERE e.user = :user")
List<Event> findAllByUser(
        @Param("user") User user);
}