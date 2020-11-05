package com.proj.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {
    private String sessionId;
    private User user;
    private Integer code;

    public LoginResult(Integer code) {
        this.code = code;
    }
}