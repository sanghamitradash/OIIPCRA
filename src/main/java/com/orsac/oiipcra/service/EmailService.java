package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.MailDto;

public interface EmailService {
    Boolean sendHtmlMail(MailDto mailDto);
}
