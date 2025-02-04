package com.fayupable.mail_sender.dto;

import com.fayupable.mail_sender.Enum.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String email;
    private String password;
    private String username;
    private Set<Role> role;
    private String verificationCode;

}
