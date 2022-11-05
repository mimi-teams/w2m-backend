package com.mimi.w2m.backend.domain;

import com.mimi.w2m.backend.domain.type.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "mimi_user")
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    @Comment("이름")
    private String name;

    @Column(name = "email", length = 200, nullable = false)
    @Comment("이메일")
    private String email;

    @Column(name = "role", length = 50, nullable = false)
    @Comment("역할")
    private Role role;

    @Builder
    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    protected User() {
    }

    public User update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }
}
