package com.fayupable.mail_sender.service;

import com.fayupable.mail_sender.dto.LoginUserDto;
import com.fayupable.mail_sender.dto.UserDto;
import com.fayupable.mail_sender.dto.VerifyUserDto;
import com.fayupable.mail_sender.entity.User;
import com.fayupable.mail_sender.request.AddUserRequest;
import com.fayupable.mail_sender.request.EmailRequest;
import com.fayupable.mail_sender.response.MailSenderResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {
    ResponseEntity<MailSenderResponse> login(LoginUserDto request);

    ResponseEntity<MailSenderResponse> validateTokenFromHeader(HttpServletRequest request);

    ResponseEntity<MailSenderResponse> logout(HttpServletRequest request);

    UserDto addUser(AddUserRequest addUserRequest);

    void verifyUser(VerifyUserDto input);

    void resendVerificationCode(EmailRequest email);

    void sendVerificationEmail(User user);

    List<User> getAllUsers();
}
