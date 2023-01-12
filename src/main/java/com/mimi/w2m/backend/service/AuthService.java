package com.mimi.w2m.backend.service;

import com.mimi.w2m.backend.config.constants.Constants;
import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.config.exception.IllegalAccessException;
import com.mimi.w2m.backend.domain.BlockedJwt;
import com.mimi.w2m.backend.domain.type.Role;
import com.mimi.w2m.backend.dto.auth.CurrentUserInfo;
import com.mimi.w2m.backend.dto.security.LoginInfo;
import com.mimi.w2m.backend.repository.BlockedJwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * AuthService(Authorization 확인)
 *
 * @author teddy
 * @version 1.0.0
 * @since 2022/11/25
 **/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final EventService eventService;
    private final EventParticipantService eventParticipantService;
    private final BlockedJwtRepository blockedJwtRepository;

    private final HttpServletRequest request;

    /**
     * 적절한 이용자의 로그인인지 확인한다
     *
     * @author teddy
     * @since 2022/12/01
     **/
    public void isValidLogin(LoginInfo info, UUID id, Role role) throws IllegalAccessException {
        if (Objects.isNull(info) || !Objects.equals(info.loginId(), id) || !Objects.equals(info.role(), role)) {
            final var msg = String.format("[AuthService] Illegal Access(id=%s, role=%s)", id, role);
            throw new IllegalAccessException(msg);
        }
    }

    public void isInEvent(CurrentUserInfo info, UUID eventId) throws IllegalAccessException {
        try {
            eventParticipantService.get(eventId, info.userId(), info.role());
        } catch (RuntimeException e) {
            final var msg = String.format("[AuthService] Illegal Access(id=%s, role=%s, event=%s)", info.userId(),
                    info.role(), eventId);
            throw new IllegalAccessException(msg);
        }
    }

    public void isHost(UUID userId, UUID eventId) throws IllegalAccessException {
        final var event = eventService.getEvent(eventId);
        final var user = userService.getUser(userId);

        if (!Objects.equals(event.getHost(), user)) {
            throw new IllegalAccessException(String.format("[AuthService] Illegal Access(id=%s, event=%s)", userId, eventId));
        }
    }

    @Deprecated
    public LoginInfo getLoginInfo(HttpSession httpSession) throws EntityNotFoundException {
        final var info = (LoginInfo) httpSession.getAttribute(LoginInfo.key);
        if (Objects.isNull(info)) {
            throw new EntityNotFoundException("[AuthService] There are no login users", "로그인한 이용자가 없습니다");
        }
        return info;
    }

    /**
     * 현재 유저 정보 반환
     *
     * @author yeh35
     * @since 2022-12-14
     */
    public CurrentUserInfo getCurrentUserInfo() {
        Object attribute = request.getAttribute(Constants.CURRENT_USER);

        if (attribute == null) {
            throw new EntityNotFoundException("[AuthService] There are no login users", "로그인한 이용자가 없습니다");
        }
        return (CurrentUserInfo) attribute;
    }

    public void logoutToken(String token) {
        if (!blockedJwtRepository.existsById(token)) {
            blockedJwtRepository.save(BlockedJwt
                    .builder()
                    .token(token)
                    .expiredDate(LocalDateTime.now())
                    .build()
            );
        }
    }
}