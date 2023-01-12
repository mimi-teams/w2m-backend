package com.mimi.w2m.backend.converter.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TimeZoneJsonConverterTest
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/10
 **/
class TimeZoneJsonConverterTest {
    @Test
    @DisplayName("Serialize Test")
    void testSerialize() {
        final var timezone = TimeZone.getTimeZone("Asia/Seoul");
        assertThat(timezone.getID()).isEqualTo("Asia/Seoul");
    }
}