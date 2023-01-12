package com.mimi.w2m.backend.repository;

import com.mimi.w2m.backend.domain.Oauth2Token;
import com.mimi.w2m.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Oauth2Repository
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/09
 **/
public interface Oauth2Repository extends JpaRepository<Oauth2Token, Long> {
    @Query("SELECT token FROM Oauth2Token token WHERE token.user = :user")
    List<Oauth2Token> findByUser(@Param("user") User user);
}
