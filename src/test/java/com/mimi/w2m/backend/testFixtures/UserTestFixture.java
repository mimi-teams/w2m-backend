package com.mimi.w2m.backend.testFixtures;

import com.mimi.w2m.backend.domain.User;

public class UserTestFixture {

    public static User createUser(String name) {
        return User.builder()
                .name(name)
                .email(name + "@gmail.com")
                .build();
    }

    public static User createUser() {
        return createUser("테스트 유저");
    }


}
