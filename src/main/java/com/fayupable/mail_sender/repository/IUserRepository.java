package com.fayupable.mail_sender.repository;

import com.fayupable.mail_sender.entity.User;
import com.fayupable.mail_sender.request.EmailRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);

    Optional<User> findByVerificationCode(String verificationCode);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithOptional(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmailWithOptionalRequest(EmailRequest email);
}
