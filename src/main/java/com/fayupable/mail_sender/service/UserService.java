package com.fayupable.mail_sender.service;

import com.fayupable.mail_sender.Enum.Role;
import com.fayupable.mail_sender.dto.LoginUserDto;
import com.fayupable.mail_sender.dto.UserDto;
import com.fayupable.mail_sender.dto.VerifyUserDto;
import com.fayupable.mail_sender.entity.User;
import com.fayupable.mail_sender.mapper.UserMapper;
import com.fayupable.mail_sender.repository.IUserRepository;
import com.fayupable.mail_sender.request.AddUserRequest;
import com.fayupable.mail_sender.request.EmailRequest;
import com.fayupable.mail_sender.response.JwtResponse;
import com.fayupable.mail_sender.response.MailSenderResponse;
import com.fayupable.mail_sender.security.jwt.JwtUtils;
import com.fayupable.mail_sender.security.user.UserDetails;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;

    @Override
    public ResponseEntity<MailSenderResponse> login(LoginUserDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateTokenForUser(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return ResponseEntity.ok(new MailSenderResponse("Login success", new JwtResponse(userDetails.getId(), jwt)));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MailSenderResponse("Invalid credentials", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MailSenderResponse("Login failed: " + e.getMessage(), null));
        }
    }

    public boolean validateToken(String token) {
        try {
            if (jwtUtils.isTokenBlacklisted(token)) {
                return false;
            }
            return jwtUtils.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public ResponseEntity<MailSenderResponse> validateTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = validateToken(token);
            return ResponseEntity.ok(new MailSenderResponse("Token is valid", isValid));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MailSenderResponse("Invalid token", false));
        }
    }

    @Override
    public ResponseEntity<MailSenderResponse> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                jwtUtils.blacklistToken(token);

                SecurityContextHolder.clearContext();

                return ResponseEntity.ok(new MailSenderResponse("Logout successful", null));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MailSenderResponse("No token provided", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MailSenderResponse("Logout failed: " + e.getMessage(), null));
        }
    }


    @Override
    public UserDto addUser(AddUserRequest addUserRequest) {
        return Optional.of(addUserRequest)
                .map(this::createUser)
                .map(userRepository::save)
                .map(userMapper::fromUser)
                .orElseThrow(() -> new RuntimeException("User not created"));

    }

    private User createUser(AddUserRequest addUserRequest) {
        User user = new User();
        user.setEmail(addUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(addUserRequest.getPassword()));
        user.setUserName(addUserRequest.getUsername());
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpirationTime(LocalDateTime.now().plusMinutes(5));
        sendVerificationEmail(user);
        return user;
    }

    @Override
    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmailWithOptional(input.getEmail());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        if (user.getVerificationCodeExpirationTime() == null || user.getVerificationCodeExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(input.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpirationTime(null);
        userRepository.save(user);
    }

    @Override
    public void resendVerificationCode(EmailRequest email) {
        Optional<User> optionalUser = userRepository.findByEmailWithOptional(email.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpirationTime(LocalDateTime.now().plusHours(1));
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    @Override
    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
