package com.mimi.w2m.backend.service;

import com.mimi.w2m.backend.config.exception.EntityNotFoundException;
import com.mimi.w2m.backend.config.exception.InvalidValueException;
import com.mimi.w2m.backend.domain.Event;
import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.dto.user.UserRequestDto;
import com.mimi.w2m.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * 유저 정보 처리
 *
 * @author teddy, yeh35
 * @since 2022-12-04
 **/


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventParticipantAbleTimeRepository eventParticipantAbleTimeRepository;
    private final EventSelectableParticipleTimeRepository eventSelectableParticipleTimeRepository;
    private final GuestRepository guestRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 이용자를 등록한다. 저장한다
     *
     * @author yeh35
     * @since 2022-11-01
     */
    @Transactional
    protected User registerUser(String name, String email) {
        if (getUserByEmail(email).isPresent()) {
            throw new InvalidValueException(String.format("이미 등록된 유저입니다: email=%s", email), "이미 등록된 유저입니다");
        }

        final var user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    /**
     * 가입된 이용자 정보 삭제
     *
     * @author teddy
     * @since 2022/11/19
     **/
    @Transactional
    public void deleteReal(UUID userId) throws EntityNotFoundException {
        final var user = getUser(userId);
        final var eventsCreatedByUser = eventRepository.findAllByHost(user);
        eventsCreatedByUser.forEach((Event event) -> {
            eventSelectableParticipleTimeRepository.deleteByEvent(event);
            final var participants = eventParticipantRepository.findAllInEvent(event);
            eventParticipantAbleTimeRepository.deleteByEventParticipantList(participants);
            eventParticipantRepository.deleteAll(participants);
            guestRepository.deleteAll(guestRepository.findAllByEvent(event));
        });
        eventRepository.deleteAll(eventsCreatedByUser);
        calendarRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    /**
     * 가입된 이용자 정보 가져오기
     *
     * @author yeh35
     * @since 2022-11-01
     */
    public User getUser(UUID id) throws EntityNotFoundException {
        final var user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            final var msg = String.format("[UserService] Entity Not Found" + "(id=%s)", id);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 이용자 삭제하기(deletedAt 만 설정)
     *
     * @author teddy
     * @since 2022/11/27
     **/
    @Transactional
    public void deleteNotReal(UUID userId) throws EntityNotFoundException {
        var user = getUser(userId);
        user.delete();
    }

    public User getByEmail(String email) throws EntityNotFoundException {
        final var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            final var msg = String.format("[UserService] Entity Not Found" + "(email=%s)", email);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 이용자 정보 변경하기. 현재 Email 을 변경할 수는 없다
     *
     * @author teddy
     * @since 2022/11/19
     **/
    @Transactional
    public User update(UUID userId, UserRequestDto requestDto) throws EntityNotFoundException {
        final var user = getUser(userId);

        user.updateName(requestDto.getName());

        if (requestDto.getEmail() != null) {
            final Optional<User> byEmail = userRepository.findByEmail(requestDto.getEmail());
            if (byEmail.isPresent()) {
                throw new InvalidValueException("이미 사용중인 이메일 입니다.", "이미 사용중인 이메일 입니다.");
            }
            user.updateEmail(requestDto.getEmail());
        }

        return user;
    }
}