package com.fayupable.mail_sender.request;

import lombok.Data;

@Data
public class AddUserRequest {
    private String email;
    private String password;
    private String username;
}
