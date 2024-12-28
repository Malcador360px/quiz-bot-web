package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.FetchKeywords;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.configuration.enums.RequestKeywords;
import com.web.quiz_bot.domain.User;
import com.web.quiz_bot.request.Request;
import com.web.quiz_bot.service.QuizServerService;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.RequestUtil;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import com.web.quiz_bot.vaadin.view.form.RedactQuizForm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Route(value = "quiz-manager", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
public class QuizManagerView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {

    private final Notification error = new Notification();
    private final Notification success = new Notification();
    private final UserService userService;
    private final QuizServerService quizServerService;

    @Autowired
    public QuizManagerView(UserService userService, QuizServerService quizServerService) {
        this.userService = userService;
        this.quizServerService = quizServerService;
        addClassName("quiz-manager-view");
        error.setPosition(Notification.Position.MIDDLE);
        error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        success.setPosition(Notification.Position.MIDDLE);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        refresh();
    }

    private void refresh() {
        JSONObject quizzes = VaadinViewUtil.fetchQuizzes(userService, quizServerService, error, getLocale());
        NormalButton newQuiz = new NormalButton(new Icon(VaadinIcon.CLIPBOARD_TEXT),
                e -> UI.getCurrent().navigate(NewQuizView.class));
        NormalButton copyQuiz = new NormalButton(new Icon(VaadinIcon.COPY));
        Button refreshQuizzes = new Button(new Icon(VaadinIcon.REFRESH), c -> refresh());
        newQuiz.setText(getTranslation("quiz_manager.new_quiz"));
        copyQuiz.setText(getTranslation("quiz_manager.copy_quiz"));
        copyQuiz.setEnabled(false);
        HorizontalLayout buttons = new HorizontalLayout(newQuiz, copyQuiz, refreshQuizzes);
        if (quizzes == null) {
            add(buttons);
            return;
        }
        try {
            this.removeAll();
        } catch (IllegalArgumentException ignore) {
        }
        for (Object table : quizzes.getJSONArray(JSONKeys.TABLES.toString())) {
            JSONObject data = (JSONObject) table;
            String tableName = data.getString(JSONKeys.TABLE_NAME.toString());
            H4 tableNameH4 = new H4(tableName);
            Details tableInfo = new Details(getTranslation("quiz_manager.table_info"));
            tableInfo.addContent(new HorizontalLayout(new Text(String.format(getTranslation("quiz_manager.all_entries"),
                    data.getInt(JSONKeys.ALL_ENTRIES.toString())))));
            tableInfo.addContent(new HorizontalLayout(new Text(String.format(getTranslation("quiz_manager.new_entries"),
                    data.getInt(JSONKeys.NEW_ENTRIES.toString())))));
            Details telegramBots = new Details(getTranslation("quiz_manager.telegram_bots"));
            for(Object botUsername : data.getJSONArray(JSONKeys.BOTS.toString())) {
                Button resume = new Button(new Icon(VaadinIcon.PLAY));
                resume.setEnabled(false);
                Button pause = new Button(new Icon(VaadinIcon.PAUSE));
                pause.setEnabled(false);
                telegramBots.addContent(new HorizontalLayout(
                        new Text(botUsername.toString()),
                        resume, pause
                ));
            }
            Button download = new Button(new Icon(VaadinIcon.DOWNLOAD), e -> {
                Dialog dialog = new Dialog();
                Select<String> fileFormat = new Select<>();
                fileFormat.setItems(
                        capitalize(FetchKeywords.EXCEL.toString()),
                        capitalize(FetchKeywords.CSV.toString())
                );
                fileFormat.setValue(capitalize(FetchKeywords.EXCEL.toString()));
                Button confirm = new Button(getTranslation("quiz_manager.confirm"), d -> {
                    Locale userLocale = UI.getCurrent().getLocale();
                    if (fileFormat.getValue().toLowerCase().equals(FetchKeywords.EXCEL.toString())) {
                        final StreamResource resource = new StreamResource(
                                tableName + ".xlsx", () -> fetchExcel(tableName, userLocale));
                        final StreamRegistration registration = VaadinSession.getCurrent()
                                .getResourceRegistry().registerResource(resource);
                        UI.getCurrent().getPage().open(registration.getResourceUri().toString());
                    } else if (fileFormat.getValue().toLowerCase().equals(FetchKeywords.CSV.toString())) {
                        final StreamResource resource = new StreamResource(
                                tableName + ".csv", () -> fetchCsv(tableName, userLocale));
                        final StreamRegistration registration = VaadinSession.getCurrent()
                                .getResourceRegistry().registerResource(resource);
                        UI.getCurrent().getPage().open(registration.getResourceUri().toString());
                    }
                    dialog.close();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new Button(getTranslation("quiz_manager.cancel"), d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                HorizontalLayout dialogButtons = new HorizontalLayout(confirm, cancel);
                dialog.add(new VerticalLayout(fileFormat, dialogButtons));
                dialog.open();
            });
            Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> {
                Dialog dialog = new Dialog();
                JSONObject quiz = fetchQuiz(tableName, UI.getCurrent().getLocale());
                if (quiz == null) {
                    return;
                }
                RedactQuizForm redactQuizForm = new RedactQuizForm(quiz);
                Button confirm = new Button(getTranslation("quiz_manager.confirm"), d -> {
                    dialog.close();
                    User currentUser = VaadinViewUtil.getCurrentUser(userService);
                    Request request = new Request(currentUser.getId(), RequestKeywords.UPDATE);
                    JSONObject updateJson = new JSONObject();
                    updateJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
                    updateJson.put(JSONKeys.QUIZ_JSON.toString(), redactQuizForm.edit());
                    request.setUpdateJson(updateJson);
                    try {
                        byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
                        if (response != null) {
                            VaadinViewUtil.openNotification(success, "success");
                        }
                    } catch (URISyntaxException ex) {
                        System.err.println("Cannot convert URL to URI");
                        VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
                    } catch (NullPointerException er) {
                        System.err.println("No servers available");
                        VaadinViewUtil.openNotification(error, getTranslation("util.no_servers_available"));
                    } catch (Exception ec) {
                        System.err.println("Unexpected exception: " + ec.getMessage());
                        VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
                    }
                    refresh();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new Button(getTranslation("quiz_manager.cancel"), d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                HorizontalLayout dialogButtons = new HorizontalLayout(confirm, cancel);
                dialog.add(new VerticalLayout(redactQuizForm), dialogButtons);
                dialog.open();

            });
            Button delete = new Button(new Icon(VaadinIcon.CLOSE), e -> {
                Dialog dialog = new Dialog();
                Button confirm = new Button(getTranslation("quiz_manager.confirm"), d -> {
                    deleteQuiz(data.getString(JSONKeys.TABLE_NAME.toString()));
                    dialog.close();
                    refresh();
                });
                confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new Button(getTranslation("quiz_manager.cancel"), d -> dialog.close());
                cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
                HorizontalLayout dialogButtons = new HorizontalLayout(confirm, cancel);
                dialogButtons.setSizeFull();
                dialog.add(new VerticalLayout(new Text(
                        getTranslation("quiz_manager.delete_conformation")), dialogButtons));
                dialog.open();
            });
            HorizontalLayout layout = new HorizontalLayout(tableNameH4, tableInfo,
                    telegramBots, new HorizontalLayout(download, edit, delete));
            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.setWidth("60%");
            add(layout, new Hr());
        }
        add(buttons);
    }

    private InputStream fetchExcel(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.EXCEL);
        if (response != null) {
            return new ByteArrayInputStream(response);
        }
        return null;
    }

    private InputStream fetchCsv(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.CSV);
        if (response != null) {
            return new ByteArrayInputStream(response);
        }
        return null;
    }

    private JSONObject fetchQuiz(String tableName, Locale locale) {
        byte[] response = fetch(tableName, locale, FetchKeywords.QUIZ);
        if (response != null) {
            return new JSONObject(new String(response, StandardCharsets.UTF_8));
        }
        return null;
    }

    private byte[] fetch(String tableName, Locale locale, FetchKeywords fetchWhat) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            VaadinViewUtil.openNotification(error, getTranslation("util.not_authenticated"));
        } else {
            JSONObject fetchJson = new JSONObject();
            fetchJson.put(JSONKeys.FETCH_WHAT.toString(), fetchWhat.toString());
            fetchJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
            return VaadinViewUtil.fetch(currentUser, fetchJson, quizServerService, error, locale);
        }
        return null;
    }

    private void deleteQuiz(String tableName) {
        User currentUser = VaadinViewUtil.getCurrentUser(userService);
        if (currentUser == null) {
            VaadinViewUtil.openNotification(error, getTranslation("util.not_authenticated"));
        } else {
            try {
                JSONObject deleteJson = new JSONObject();
                deleteJson.put(JSONKeys.TABLE_NAME.toString(), tableName);
                Request request = new Request(currentUser.getId(), RequestKeywords.DELETE);
                request.setDeleteJson(deleteJson);
                byte[] response = RequestUtil.sendRequest(request, quizServerService.getRandomServerURL().toURI());
                if (response == null) {
                    VaadinViewUtil.openNotification(error, getTranslation("util.server_not_responding"));
                } else {
                    VaadinViewUtil.openNotification(success, getTranslation("quiz_manager.quiz_deleted"));
                }
            } catch (URISyntaxException e) {
                System.err.println(e.getMessage());
                VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
            } catch (NullPointerException er) {
                VaadinViewUtil.openNotification(error, getTranslation("util.no_servers_available"));
            } catch (Exception ex) {
                System.err.println("Unexpected exception: " + ex.getMessage());
                VaadinViewUtil.openNotification(error, getTranslation("util.something_went_wrong"));
            }
        }
    }

    private static String capitalize(String str)
    {
        if(str == null || str.length()<=1) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.setEnabled(VaadinViewUtil.getCurrentUser(userService).isVerified());
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        this.removeAll();
        refresh();
    }
}
