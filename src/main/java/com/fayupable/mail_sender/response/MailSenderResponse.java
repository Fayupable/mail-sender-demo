package com.fayupable.mail_sender.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailSenderResponse {
    private String message;
    private Object data;
}
