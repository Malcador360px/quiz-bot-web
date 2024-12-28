package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import com.web.quiz_bot.vaadin.view.form.event.CancelEvent;
import com.web.quiz_bot.vaadin.view.form.event.SaveEvent;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class QuizSettingsForm extends VerticalLayout implements LocaleChangeObserver {

    private final SmallTextField quizName = new SmallTextField();
    private final Select<TextField> botMessenger = new Select<>();
    private final SmallTextField telegramBot = new SmallTextField();
    private final SmallTextField whatsappBot = new SmallTextField();
    private final SmallTextField botName = new SmallTextField();
    private final SmallTextField botUsername = new SmallTextField();
    private final Checkbox multipleRegistration = new Checkbox(getTranslation("settings_form.multiple_registration"));
    private final Label label = new Label(getTranslation("settings_form.label"));
    private final NormalButton newColumn = new NormalButton(new Icon(VaadinIcon.PLUS));

    private final List<TextField> columnNames = new ArrayList<>();
    private final List<Button> removeColumnButtons = new ArrayList<>();
    private final VerticalLayout quizSettings = new VerticalLayout();
    private final VerticalLayout tableSettings = new VerticalLayout();
    public final DataProvider<TextField, Void> tableColumnDataProvider;
    public boolean multipleRegistrationValue = false;

    public QuizSettingsForm() {
        tableColumnDataProvider = DataProvider.fromCallbacks(
                query -> columnNames.stream(),
                query -> columnNames.size()
        );
        columnNames.add(new TextField());

        setAlignItems(Alignment.CENTER);
        configureQuizSettings();
        configureTableSettings();
        add(new HorizontalLayout(quizSettings, tableSettings));
    }

    private void configureQuizSettings() {
        quizName.setPlaceholder(getTranslation("settings_form.quiz_name_placeholder"));
        quizName.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        multipleRegistration.setValue(false);
        multipleRegistration.addValueChangeListener(c -> this.multipleRegistrationValue = c.getValue());
        multipleRegistration.setSizeFull();
        botMessenger.setReadOnly(true);
        botMessenger.setItems(telegramBot, whatsappBot);
        botMessenger.setValue(telegramBot);
        botMessenger.setItemLabelGenerator(TextField::getValue);
        botMessenger.setSizeFull();
        telegramBot.setValue(getTranslation("settings_form.telegram_bot"));
        whatsappBot.setValue(getTranslation("settings_form.whatsapp_bot"));
        botName.setReadOnly(true);
        botName.setPlaceholder(getTranslation("settings_form.bot_name_placeholder"));
        botName.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        botUsername.setReadOnly(true);
        botUsername.setPlaceholder(getTranslation("settings_form.bot_username_placeholder"));
        botUsername.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        quizSettings.add(quizName, multipleRegistration, botMessenger, botName, botUsername);
    }

    private void configureTableSettings() {
        newColumn.addClickListener(e -> {
            HorizontalLayout column = new HorizontalLayout();
            SmallTextField columnName = new SmallTextField();
            columnName.setValueChangeMode(ValueChangeMode.EAGER);
            columnName.addValueChangeListener(c -> {
                columnName.setInvalid(false);
                for (TextField name : columnNames) {
                    if ((!c.getSource().isEmpty() && !name.equals(c.getSource()))
                            && ((name.isEmpty() && c.getValue().equals(name.getPlaceholder()))
                            || c.getValue().equals(name.getValue()))) {
                        columnName.setErrorMessage(getTranslation("settings_form.column_name_already_exists"));
                        columnName.setInvalid(true);
                        break;
                    }
                }
                tableColumnDataProvider.refreshItem(columnName);
            });
            columnName.setPlaceholder(String.format("%s %s",
                    getTranslation("settings_form.column_placeholder"),
                    tableSettings.getComponentCount() - 1));
            columnNames.add(columnName);
            tableColumnDataProvider.refreshAll();
            NormalButton removeColumn = new NormalButton(new Icon(VaadinIcon.MINUS),
                    c -> {
                        tableSettings.remove(column);
                        columnNames.remove(columnName);
                        for (int i = 1; i < columnNames.size(); i++) {
                            columnNames.get(i).setPlaceholder(String.format("%s %s",
                                    getTranslation("settings_form.column_placeholder"),
                                    i));
                        }
                        tableColumnDataProvider.refreshAll();
                    });
            removeColumn.setText(getTranslation("settings_form.remove_column_button"));
            removeColumnButtons.add(removeColumn);
            column.add(columnName, removeColumn);
            tableSettings.addComponentAtIndex(tableSettings.getComponentCount() - 1, column);
        });
        newColumn.setText(getTranslation("settings_form.new_column_button"));
        tableSettings.add(label, newColumn);
        newColumn.click();
    }

    public JSONObject save() {
        JSONObject json = new JSONObject();
        TextField quizName = (TextField) quizSettings.getComponentAt(0);
        if (quizName.isEmpty()) {
            json.put(JSONKeys.TABLE_NAME.toString(), quizName.getPlaceholder());
        } else {
            json.put(JSONKeys.TABLE_NAME.toString(), quizName.getValue());
        }
        tableSettings.getChildren().limit(tableSettings.getComponentCount() - 1)
                .skip(1).map(t -> (HorizontalLayout) t).forEach(t -> {
                    TextField columnName = (TextField) t.getComponentAt(0);

                    if (columnName.isEmpty()) {
                        json.put(columnName.getPlaceholder(), "Auto");
                    } else {
                        json.put(columnName.getValue(), "Auto");
                    }
                });
        return json;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        quizName.setPlaceholder(getTranslation("settings_form.quiz_name_placeholder"));
        botName.setPlaceholder(getTranslation("settings_form.bot_name_placeholder"));
        botUsername.setPlaceholder(getTranslation("settings_form.bot_username_placeholder"));
        multipleRegistration.setLabel(getTranslation("settings_form.multiple_registration"));
        telegramBot.setValue(getTranslation("settings_form.telegram_bot"));
        whatsappBot.setValue(getTranslation("settings_form.whatsapp_bot"));
        label.setText(getTranslation("settings_form.label"));
        for (int i = 1; i < columnNames.size(); i++) {
            columnNames.get(i).setPlaceholder(String.format("%s %s",
                    getTranslation("settings_form.column_placeholder"),
                    i));
            tableColumnDataProvider.refreshItem(columnNames.get(i));
        }
        for (Button removeColumnButton : removeColumnButtons) {
            removeColumnButton.setText(getTranslation("settings_form.remove_column_button"));
        }
        newColumn.setText(getTranslation("settings_form.new_column_button"));
    }
}
