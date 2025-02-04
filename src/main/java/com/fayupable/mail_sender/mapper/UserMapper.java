package com.fayupable.mail_sender.mapper;

import com.fayupable.mail_sender.dto.UserDto;
import com.fayupable.mail_sender.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUserName());
        userDto.setRole(user.getRoles());
        userDto.setPassword(user.getPassword());
        userDto.setVerificationCode(user.getVerificationCode());
        return userDto;
    }
}
