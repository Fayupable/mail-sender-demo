package com.fayupable.mail_sender.controller;

import com.fayupable.mail_sender.dto.LoginUserDto;
import com.fayupable.mail_sender.dto.VerifyUserDto;
import com.fayupable.mail_sender.entity.User;
import com.fayupable.mail_sender.request.AddUserRequest;
import com.fayupable.mail_sender.request.EmailRequest;
import com.fayupable.mail_sender.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AddUserRequest request) {
        return ResponseEntity.ok(userService.addUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserDto verificationCode) {
        userService.verifyUser(verificationCode);
        return ResponseEntity.ok("User verified");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody EmailRequest email) {
        userService.resendVerificationCode(email);
        return ResponseEntity.ok("Verification code resent");
    }

}
