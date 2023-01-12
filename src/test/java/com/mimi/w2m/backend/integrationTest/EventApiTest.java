package com.mimi.w2m.backend.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class EventApiTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("캘린더 이벤트 생성 테스트")
    void testCalendarEventCreate() {
        //given

        //when

        //then
    }
}