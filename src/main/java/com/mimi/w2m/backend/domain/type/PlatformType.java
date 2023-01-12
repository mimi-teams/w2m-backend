package com.mimi.w2m.backend.domain.type;

public enum PlatformType {

    /**
     * 카카오 간변 로그인
     */
    KAKAO(Value.KAKAO_KEY);
    private final String key;

    PlatformType(String key) {
        this.key = key;
    }

    private static class Value {
        private final static String KAKAO_KEY = "KAKAO";
    }

}
