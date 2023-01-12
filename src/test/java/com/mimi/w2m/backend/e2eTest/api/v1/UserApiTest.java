package com.mimi.w2m.backend.e2eTest.api.v1;

import com.mimi.w2m.backend.domain.User;
import com.mimi.w2m.backend.dto.user.UserRequestDto;
import com.mimi.w2m.backend.e2eTest.End2EndTest;
import com.mimi.w2m.backend.repository.UserRepository;
import com.mimi.w2m.backend.testFixtures.UserTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"unused", "NonAsciiCharacters"})
public class UserApiTest extends End2EndTest {

    @Autowired
    protected UserRepository userRepository;


    @Test
    void 이용자_정보_반환() throws Exception {
        // given
        User user = userRepository.save(UserTestFixture.createUser("가나다"));

        final String token = login(user);

        //when & then
        mockMvc.perform(
                        get("/v1/users/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(user.getName()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
        ;
    }

    @Test
    void 이용자_정보_업데이트() throws Exception {
        // given
        User user = userRepository.save(UserTestFixture.createUser("가나"));

        final String token = login(user);

        final var userRequestDto = new UserRequestDto("가나다라", null);

        //when & then
        mockMvc.perform(
                        patch("/v1/users/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(userRequestDto.getName()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
        ;
    }

    @Test
    void 이용자_정보_업데이트_이메일까지() throws Exception {
        // given
        User user = userRepository.save(UserTestFixture.createUser("가나"));

        final String token = login(user);

        final var userRequestDto = new UserRequestDto("가나다라", "가나다라@naver.com");

        //when & then
        mockMvc.perform(
                        patch("/v1/users/me")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(userRequestDto.getName()))
                .andExpect(jsonPath("$.data.email").value(userRequestDto.getEmail()))
        ;
    }

    @Test
    void 이용자_로그아웃() throws Exception {
        // given
        User user = userRepository.save(UserTestFixture.createUser("가나"));

        final String token = login(user);

        //when & then
        mockMvc.perform(
                        post("/v1/users/me/logout")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }



    @Test
    @Transactional
    void 이용자_탈퇴() throws Exception {
        // given
        User user = userRepository.save(UserTestFixture.createUser("가나"));

        final String token = login(user);

        final var userRequestDto = new UserRequestDto("가나다라", "가나다라@naver.com");

        //when & then
        mockMvc.perform(
                        post("/v1/users/me/leave")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                )
                .andExpect(status().isOk())
        ;
    }

}
