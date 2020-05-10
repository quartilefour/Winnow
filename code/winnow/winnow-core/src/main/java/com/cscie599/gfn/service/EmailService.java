package com.cscie599.gfn.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendEmail(SimpleMailMessage email);
    SimpleMailMessage createEmail(String to, String subject, String text);
}
