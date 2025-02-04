package com.fayupable.mail_sender.service;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationEmail(String to, String subject, String text) throws MessagingException;
}
