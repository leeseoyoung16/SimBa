package com.simba.project01.user;

import lombok.Getter;

@Getter
public class UserResponse
{
    private Long id;
    private String username;
    private String role;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.id = user.getId();
    }
}
