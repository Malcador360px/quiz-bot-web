package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.configuration.enums.Formats;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringComponent
@UIScope
public class QuizForm extends VerticalLayout implements LocaleChangeObserver {

    private final NormalButton newQuestion = new NormalButton(new Icon(VaadinIcon.PLUS), e -> addBlock());

    private final List<VerticalLayout> blocks = new ArrayList<>();
    private final QuizSettingsForm quizSettings;

    public QuizForm(QuizSettingsForm quizSettings) {
        this.quizSettings = quizSettings;
        setAlignItems(Alignment.CENTER);
        addQuestionForm();
    }

    private void addQuestionForm() {
        newQuestion.setText(getTranslation("request_form.new_question_button"));
        addBlock();
        add(newQuestion);
    }

    private void addBlock() {
        VerticalLayout block = new VerticalLayout();
        HorizontalLayout upperBlock = new HorizontalLayout();
        VerticalLayout middleBlock = new VerticalLayout();
        HorizontalLayout lowerBlock = new HorizontalLayout();

        NormalTextField questionField = new NormalTextField();
        questionField.setPlaceholder(getTranslation("request_form.question_placeholder"));
        questionField.setValueChangeMode(ValueChangeMode.EAGER);
        Select<TextField> tableColumn = new Select<>();
        tableColumn.setItems(quizSettings.tableColumnDataProvider);
        tableColumn.setItemLabelGenerator(t -> {
            String value = t.getValue();
            if (value.isEmpty()) {
                if (t.getPlaceholder() != null) {
                    return t.getPlaceholder();
                } else {
                    return "";
                }
            } else {
                return value;
            }
        });
        tableColumn.setPlaceholder(getTranslation("request_form.table_column_placeholder"));
        NormalButton removeBlockButton = new NormalButton(new Icon(VaadinIcon.MINUS), e -> remove(block));
        removeBlockButton.setText(getTranslation("request_form.remove_question_button"));
        upperBlock.add(questionField, tableColumn, removeBlockButton);

        NormalButton newAnswer = new NormalButton(new Icon(VaadinIcon.PLUS), e -> {
            HorizontalLayout answer = new HorizontalLayout();
            SmallTextField answerField = new SmallTextField();
            answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
            answerField.setValueChangeMode(ValueChangeMode.EAGER);
            answerField.addValueChangeListener(c -> questionField.setInvalid(false));
            NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answer));
            removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
            answer.add(answerField, removeAnswer);
            middleBlock.addComponentAtIndex(middleBlock.getComponentCount() - 1, answer);
        });
        newAnswer.setText(getTranslation("request_form.new_answer_button"));
        middleBlock.add(newAnswer);
        newAnswer.click();

        ComboBox<String> customAnswerFormat = new ComboBox<>();
        customAnswerFormat.setPlaceholder(getTranslation("request_form.custom_answer_format_placeholder"));
        customAnswerFormat.setItems(Arrays.stream(Formats.values()).map(Formats::toString).toList());
        customAnswerFormat.setValue(Formats.ANY.toString());
        customAnswerFormat.setSizeFull();
        Checkbox customAnswer = new Checkbox(getTranslation("request_form.custom_answer"));
        customAnswer.addValueChangeListener(c -> {
            customAnswerFormat.setReadOnly(!c.getValue());
            if (c.getValue()) {
                questionField.setInvalid(false);
            }
        });
        customAnswer.setSizeFull();
        customAnswer.setValue(false);
        customAnswerFormat.setReadOnly(true);
        lowerBlock.add(customAnswer, customAnswerFormat);

        block.add(upperBlock, middleBlock, lowerBlock);
        block.setAlignItems(Alignment.START);
        block.setSizeFull();
        blocks.add(block);

        if (getComponentCount() > 0) {
            addComponentAtIndex(getComponentCount() - 1, block);
        } else {
            add(block);
        }
    }

    public JSONObject save() {
        JSONObject json = new JSONObject();
        int questionOrder = 1;
        for (VerticalLayout block : blocks) {
            JSONObject content = new JSONObject();

            HorizontalLayout upperBlock = (HorizontalLayout) block.getComponentAt(0);
            TextField question = (TextField) upperBlock.getComponentAt(0);
            if (question.getValue().isEmpty()) {
                continue;
            }
            VerticalLayout middleBlock = (VerticalLayout) block.getComponentAt(1);
            middleBlock.getChildren().limit(middleBlock.getComponentCount() - 1)
                    .map(e -> (HorizontalLayout) e).forEach(e -> {
                        TextField answer = (TextField) e.getComponentAt(0);
                        if (!answer.isEmpty()) {
                            content.append(JSONKeys.ANSWERS.toString(), answer.getValue());
                        }
                    });

            HorizontalLayout lowerBlock = (HorizontalLayout) block.getComponentAt(2);
            Checkbox customAnswer = (Checkbox) lowerBlock.getComponentAt(0);
            if (customAnswer.getValue()) {
                ComboBox<String> customAnswerFormat = (ComboBox<String>) lowerBlock.getComponentAt(1);
                String format = customAnswerFormat.getValue();
                if (format == null || format.isEmpty() || format.isBlank()) {
                    format = Formats.ANY.toString();
                }
                content.append(JSONKeys.CUSTOM_ANSWER.toString(), encodeFormat(format));
            }
            if (content.isEmpty()) {
                question.setErrorMessage(getTranslation("request_form.question_err_message"));
                question.setInvalid(true);
                return null;
            }
            Select<TextField> tableColumn = (Select<TextField>) upperBlock.getComponentAt(1);
            TextField selectedField = tableColumn.getValue();
            if (selectedField == null || selectedField.getPlaceholder() == null) {
                ;
            } else if (selectedField.isEmpty()) {
                content.put(JSONKeys.TABLE_COLUMN.toString(), selectedField.getPlaceholder());
            } else {
                content.put(JSONKeys.TABLE_COLUMN.toString(), selectedField.getValue());
            }
            JSONObject wrapper = new JSONObject();
            wrapper.put(question.getValue(), content);
            json.put(String.valueOf(questionOrder++), wrapper);
        }
        if(json.isEmpty()) {
            return json;
        }
        json.put(JSONKeys.MULTIPLE_REGISTRATION.toString(), quizSettings.multipleRegistrationValue);
        return json;
    }

    private String encodeFormat(String format) {
        if (Formats.ANY.toString().equals(format)) {
            return Formats.ANY.name().toLowerCase();
        } else if (Formats.ONLY_LETTERS.toString().equals(format)) {
            return Formats.ONLY_LETTERS.name().toLowerCase();
        } else if (Formats.INTEGER.toString().equals(format)) {
            return Formats.INTEGER.name().toLowerCase();
        } else if (Formats.DATE.toString().equals(format)) {
            return Formats.DATE.name().toLowerCase();
        } else if (Formats.PERSON_NAME.toString().equals(format)) {
            return Formats.PERSON_NAME.name().toLowerCase();
        } else if (Formats.PHONE_NUMBER.toString().equals(format)) {
            return Formats.PHONE_NUMBER.name().toLowerCase();
        } else if (Formats.EMAIL_ADDRESS.toString().equals(format)) {
            return Formats.EMAIL_ADDRESS.name().toLowerCase();
        } else {
            return "unknown_format";
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        newQuestion.setText(getTranslation("request_form.new_question_button"));
        for (VerticalLayout block : blocks) {
            HorizontalLayout upperBlock = (HorizontalLayout) block.getComponentAt(0);
            TextField question = (TextField) upperBlock.getComponentAt(0);
            question.setPlaceholder(getTranslation("request_form.question_placeholder"));
            question.setErrorMessage(getTranslation("request_form.question_err_message"));
            ((Select<TextField>) upperBlock.getComponentAt(1)).setPlaceholder(
                    getTranslation("request_form.table_column_placeholder"));
            ((Button) upperBlock.getComponentAt(2)).setText(
                    getTranslation("request_form.remove_question_button"));
            VerticalLayout middleBlock = (VerticalLayout) block.getComponentAt(1);
            ((Button) middleBlock.getComponentAt(middleBlock.getComponentCount() - 1)).setText(
                    getTranslation("request_form.new_answer_button"));
            for (int i = 0; i < middleBlock.getComponentCount() - 1; i++) {
                HorizontalLayout answer = (HorizontalLayout) middleBlock.getComponentAt(i);
                ((TextField) answer.getComponentAt(0)).setPlaceholder(
                        getTranslation("request_form.answer_placeholder"));
                ((Button) answer.getComponentAt(1)).setText(
                        getTranslation("request_form.remove_answer_button"));
            }
            HorizontalLayout lowerBlock = (HorizontalLayout) block.getComponentAt(2);
            ((Checkbox) lowerBlock.getComponentAt(0)).setLabel(
                    getTranslation("request_form.custom_answer"));
            ((ComboBox<String>) lowerBlock.getComponentAt(1)).setPlaceholder(
                    getTranslation("request_form.custom_answer_format_placeholder"));
        }
    }
}
