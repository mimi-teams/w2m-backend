package com.mimi.w2m.backend.dto.event;

import com.mimi.w2m.backend.domain.type.ParticipleTime;
import com.mimi.w2m.backend.domain.type.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Schema(description = "이벤트에서 선택 가능한 시간")
public class SelectableParticipleTimeDto {

    @Schema(example = "[\"MONDAY\", \"SUNDAY\"]")
    @NotNull
    private Set<DayOfWeek> selectedDayOfWeeks;

    @Schema(example = "05:05:00")
    @NotNull
    private LocalTime beginTime;

    @Schema(example = "15:05:00")
    @NotNull
    private LocalTime endTime;

    @Builder
    public SelectableParticipleTimeDto(Set<DayOfWeek> selectedDayOfWeeks, LocalTime beginTime, LocalTime endTime) {
        this.selectedDayOfWeeks = selectedDayOfWeeks;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    protected SelectableParticipleTimeDto() {
        
    }

    /**
     * 최저 시간과 최대 시간만 남긴다.
     *
     * @author yeh35
     * @since 2022-12-31
     */
    public static SelectableParticipleTimeDto of(Set<ParticipleTime> participleTimeSet) {
        final var weekSet = new HashSet<DayOfWeek>(7);
        LocalTime beginTime = LocalTime.MAX;
        LocalTime endTime = LocalTime.MIN;

        for (ParticipleTime participleTime : participleTimeSet) {
            weekSet.add(participleTime.getWeek());

            for (TimeRange timeRange : participleTime.getRanges()) {
                if (beginTime.isAfter(timeRange.beginTime())) {
                    beginTime = timeRange.beginTime();
                }

                if (endTime.isBefore(timeRange.endTime())) {
                    endTime = timeRange.endTime();
                }
            }
        }

        return SelectableParticipleTimeDto.builder()
                .selectedDayOfWeeks(weekSet)
                .beginTime(beginTime)
                .endTime(endTime)
                .build();
    }

    public Set<ParticipleTime> toParticipleTimeSet() {

        final var resultSet = new HashSet<ParticipleTime>(selectedDayOfWeeks.size());

        for (final DayOfWeek dayOfWeek : selectedDayOfWeeks) {
            final var timeRange = new TimeRange(beginTime, endTime);
            final var participleTime = ParticipleTime.builder()
                    .week(dayOfWeek)
                    .ranges(Set.of(timeRange))
                    .build();

            resultSet.add(participleTime);
        }

        return resultSet;
    }

}
