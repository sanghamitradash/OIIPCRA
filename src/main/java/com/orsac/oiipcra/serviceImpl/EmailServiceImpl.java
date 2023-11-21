package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.dto.MailDto;
import com.orsac.oiipcra.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromUser;

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public Boolean sendHtmlMail(MailDto mailDto) {
        MimeMessage msg = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(fromUser);
            helper.setTo(mailDto.getRecipient());
            helper.setSubject(mailDto.getSubject());
            helper.setText(mailDto.getMessage(), true);
            javaMailSender.send(msg);
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
