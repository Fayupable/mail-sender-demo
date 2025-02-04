package com.fayupable.mail_sender.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginUserDto {
    private String email;
    private String password;
}
