package com.web.quiz_bot.service.listener;

import com.web.quiz_bot.configuration.UTF8Control;
import com.web.quiz_bot.domain.EmailVerificationToken;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.localization.LocalizationProvider;
import com.web.quiz_bot.service.EmailVerificationTokenService;
import com.web.quiz_bot.service.event.UserEmailConformationEvent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class UserEmailConformationListener implements ApplicationListener<UserEmailConformationEvent> {

    private static final String BUNDLE_PREFIX = "messages";
    private static final String CONFORMATION_FROM = "noreply@quiz-bot.net";
    private final EmailVerificationTokenService tokenService;
    private final JavaMailSenderImpl mailSender;

    @Autowired
    public UserEmailConformationListener(EmailVerificationTokenService tokenService,
                                         JavaMailSenderImpl mailSender) {
        this.tokenService = tokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(UserEmailConformationEvent event) {
        User user = event.getUser();
        EmailVerificationToken token = tokenService.createToken(user);
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            name = user.getUsername();
        }
        String link = getMessage("mail.link", event.getLocale()) + token.getId();
        String mailText = getMessage("mail.text", event.getLocale(), name, link);
        String subject = getMessage("mail.subject", event.getLocale());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(CONFORMATION_FROM);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailText, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (MailException ignore) {

        }
    }

    private String getMessage(String key, Locale locale, Object... params) {
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale, new UTF8Control());
        String value;
        try {
            value = bundle.getString(key);
        } catch (MissingResourceException e) {
            LoggerFactory.getLogger(LocalizationProvider.class.getName())
                    .warn("Missing resource", e);
            return  "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            MessageFormat formatter = new MessageFormat(value, locale);
            value = formatter.format(params);
        }
        return value;
    }
}
