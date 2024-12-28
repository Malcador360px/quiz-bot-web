package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.Roles;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.domain.data.UserData;
import com.web.quiz_bot.exception.UserAlreadyExists;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.service.event.UserEmailConformationEvent;
import com.web.quiz_bot.vaadin.component.CustomHr;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalPasswordField;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.view.AfterRegister;
import com.web.quiz_bot.vaadin.view.form.event.CancelEvent;
import com.web.quiz_bot.vaadin.view.form.event.SaveEvent;
import me.gosimple.nbvcxz.Nbvcxz;
import org.apache.commons.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringComponent
@UIScope
public class RegisterUserForm extends VerticalLayout implements LocaleChangeObserver {

    private final NormalTextField name = new NormalTextField(getTranslation("register.name"));
    private final NormalTextField username = new NormalTextField(getTranslation("register.username"));
    private final NormalTextField email = new NormalTextField(getTranslation("register.email"));
    private final NormalPasswordField password = new NormalPasswordField(getTranslation("register.password"));
    private final NormalPasswordField confirmPassword = new NormalPasswordField(getTranslation("register.confirm_password"));
    private final Notification usernameOrEmailAlreadyUsed = new Notification();
    private final Text notificationText = new Text(getTranslation("register.username_or_email_already_used"));
    private final NormalButton save = new NormalButton (getTranslation("register.save"), new Icon(VaadinIcon.CHECK));
    private final NormalButton  cancel = new NormalButton (getTranslation("register.cancel"), new Icon(VaadinIcon.CLOSE));

    private static final String NAME_VALIDATION_PATTERN = "^([a-zA-Z]+\\s?)+$";
    private String nameValidationErrMessage = getTranslation("register.name_err_message");
    private static final String USERNAME_VALIDATION_PATTERN = "^[a-zA-Z\\d_.-]*$";
    private String usernameValidationErrMessage = getTranslation("register.username_err_message");
    private String usernameAlreadyUsed = getTranslation("register.username_already_used");
    private String emailValidationErrMessage = getTranslation("register.email_err_message");
    private String emailAlreadyUsed = getTranslation("register.email_already_used");
    private String passwordValidationErrMessage = getTranslation("register.password_err_message");
    private String confirmPasswordValidationErrMessage = getTranslation("register.confirm_password_err_message");
    private static final int NOTIFICATION_DURATION = 5;
    public static final double MINIMAL_PASSWORD_STRENGTH = 15.0;

    private final Binder<UserData> binder = new Binder<>(UserData.class);

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final Nbvcxz nbvcxz;

    @Autowired
    public RegisterUserForm(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.nbvcxz = new Nbvcxz();
        setAlignItems(Alignment.CENTER);
        configureBinder();
        addComponents();
    }

    private void configureBinder() {
        binder.forField(name).withValidator(name -> name == null || name.equals("") || name.matches(NAME_VALIDATION_PATTERN),
                nameValidationErrMessage).bind(UserData::getName, UserData::setName);
        binder.forField(username).withValidator(username -> username.matches(USERNAME_VALIDATION_PATTERN),
                usernameValidationErrMessage).withValidator(username -> !userService.checkIfExists(username),
                usernameAlreadyUsed).bind(UserData::getUsername, UserData::setUsername);
        binder.forField(email).withValidator(email -> EmailValidator.getInstance().isValid(email),
                emailValidationErrMessage).withValidator(email -> !userService.checkIfExistsEmail(email),
                emailAlreadyUsed).bind(UserData::getEmail, UserData::setEmail);
        binder.forField(password).withValidator(password -> nbvcxz.estimate(password).getEntropy() >= MINIMAL_PASSWORD_STRENGTH,
                passwordValidationErrMessage).bind(UserData::getPassword, UserData::setPassword);
        binder.forField(confirmPassword).withValidator(password -> password.equals(this.password.getValue()),
                confirmPasswordValidationErrMessage).bind(UserData::getPassword, UserData::setPassword);
    }

    private void addComponents() {
        name.setPlaceholder(getTranslation("register.name_placeholder"));
        name.setClearButtonVisible(true);
        name.setRequired(false);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        username.setPlaceholder(getTranslation("register.username_placeholder"));
        username.setClearButtonVisible(true);
        username.setRequired(true);
        username.setValueChangeMode(ValueChangeMode.EAGER);
        email.setPlaceholder(getTranslation("register.email_placeholder"));
        email.setClearButtonVisible(true);
        email.setRequired(true);
        email.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        password.setPlaceholder(getTranslation("register.password_placeholder"));
        password.setClearButtonVisible(true);
        password.setRequired(true);
        password.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        confirmPassword.setPlaceholder(getTranslation("register.confirm_password_placeholder"));
        confirmPassword.setClearButtonVisible(true);
        confirmPassword.setRequired(true);
        confirmPassword.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        Div text = new Div(notificationText);
        NormalButton closeButton = new NormalButton(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            usernameOrEmailAlreadyUsed.close();
        });
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        usernameOrEmailAlreadyUsed.addThemeVariants(NotificationVariant.LUMO_ERROR);
        usernameOrEmailAlreadyUsed.setDuration(NOTIFICATION_DURATION);
        usernameOrEmailAlreadyUsed.setPosition(Notification.Position.MIDDLE);
        usernameOrEmailAlreadyUsed.add(layout);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(click -> register());
        save.addClickShortcut(Key.ENTER);

        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(click -> {
            fireEvent(new CancelEvent(this));
            onSuccess();
        });
        cancel.addClickShortcut(Key.ESCAPE);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setAlignItems(Alignment.CENTER);
        buttons.add(save, cancel);
        add(name, username, email, password, confirmPassword, new CustomHr(), buttons);
    }

    private void register() {
        try {
            UserData userData = new UserData();
            binder.writeBean(userData);
            userData.setRole(Roles.USER.toString());
            User user = userService.register(userData);
            eventPublisher.publishEvent(new UserEmailConformationEvent(user, UI.getCurrent().getLocale()));
            fireEvent(new SaveEvent(this));
            onSuccess();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null,
                            AuthorityUtils.createAuthorityList(user.getRole())
                    )
            );
            UI.getCurrent().navigate(AfterRegister.class);
        } catch (ValidationException e) {
            e.getValidationErrors();
        } catch (UserAlreadyExists er) {
            usernameOrEmailAlreadyUsed.open();
        }
    }

    private void onSuccess() {
        binder.readBean(null);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        name.setLabel(getTranslation("register.name"));
        name.setPlaceholder(getTranslation("register.name_placeholder"));
        username.setLabel(getTranslation("register.username"));
        username.setPlaceholder(getTranslation("register.username_placeholder"));
        email.setLabel(getTranslation("register.email"));
        email.setPlaceholder(getTranslation("register.email_placeholder"));
        password.setLabel(getTranslation("register.password"));
        password.setPlaceholder(getTranslation("register.password_placeholder"));
        confirmPassword.setLabel(getTranslation("register.confirm_password"));
        confirmPassword.setPlaceholder(getTranslation("register.confirm_password_placeholder"));
        notificationText.setText(getTranslation("register.username_or_email_already_used"));
        save.setText(getTranslation("register.save"));
        cancel.setText(getTranslation("register.cancel"));
        nameValidationErrMessage = getTranslation("register.name_err_message");
        usernameValidationErrMessage = getTranslation("register.username_err_message");
        usernameAlreadyUsed = getTranslation("register.username_already_used");
        emailValidationErrMessage = getTranslation("register.email_err_message");
        emailAlreadyUsed = getTranslation("register.email_already_used");
        passwordValidationErrMessage = getTranslation("register.password_err_message");
        confirmPasswordValidationErrMessage = getTranslation("register.confirm_password_err_message");
        configureBinder();
    }
}
