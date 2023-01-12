package com.mimi.w2m.backend.client.kakao.dto.calendar.event.type;

/**
 * KakaoCalendarEventRecurrentUpdateType : 반복 일정을 수정하는 경우(우리 이벤트는 주마다 반복된다), 넘겨줘야 하는 값
 *
 * @author teddy
 * @version 1.0.0
 * @since 2023/01/12
 **/
public enum KakaoCalendarEventRecurrentUpdateType {
    ALL, // Event Id 와 연관된 반복 일정 전체
    THIS, // ID 하나만
    THIS_AND_FOLLOWING // ID 를 포함한 이후 이벤트 전부
}
