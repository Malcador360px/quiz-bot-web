package com.web.quiz_bot.vaadin.view.form;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.web.quiz_bot.configuration.enums.Formats;
import com.web.quiz_bot.configuration.enums.JSONKeys;
import com.web.quiz_bot.vaadin.component.NormalButton;
import com.web.quiz_bot.vaadin.component.NormalTextField;
import com.web.quiz_bot.vaadin.component.SmallTextField;
import com.web.quiz_bot.vaadin.view.form.event.SaveEvent;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedactQuizForm extends VerticalLayout {

    private final List<VerticalLayout> blocks = new ArrayList<>();
    private final List<String> tableColumns = new ArrayList<>();
    private final boolean multipleRegistrationValue;

    public RedactQuizForm(JSONObject quizJson) {
        List<QuestionRecord> quiz = new ArrayList<>();
        multipleRegistrationValue = quizJson.getBoolean(JSONKeys.MULTIPLE_REGISTRATION.toString());
        for (int i = 1; i < quizJson.keySet().size(); i++) {
            JSONObject questionJson = quizJson.getJSONObject(String.valueOf(i));
            String question = questionJson.keys().next();
            JSONObject questionInfo = questionJson.getJSONObject(question);
            List<String> answers = new ArrayList<>();
            if (questionInfo.has(JSONKeys.ANSWERS.toString())) {
                answers = questionInfo.getJSONArray(JSONKeys.ANSWERS.toString()).toList()
                        .stream().map(a -> (String) a).toList();
            }
            List<String> customAnswer = new ArrayList<>();
            if (questionInfo.has(JSONKeys.CUSTOM_ANSWER.toString())) {
                customAnswer = questionInfo.getJSONArray(JSONKeys.CUSTOM_ANSWER.toString()).toList()
                        .stream().map(a -> (String) a).toList();
            }
            String tableColumn = "";
            if (questionInfo.has(JSONKeys.TABLE_COLUMN.toString())) {
                tableColumn = questionInfo.getString(JSONKeys.TABLE_COLUMN.toString());
            }
            tableColumns.add(tableColumn);
            quiz.add(new QuestionRecord(question, answers, customAnswer, tableColumn));
        }
        for (QuestionRecord record : quiz) {
            addBlock(record.question, record.answers, record.customAnswer, record.tableColumn);
        }
    }

    private void addBlock(String question, List<String> answers,
                          List<String> customAnswer, String tableColumn) {
        VerticalLayout block = new VerticalLayout();
        HorizontalLayout upperBlock = new HorizontalLayout();
        VerticalLayout middleBlock = new VerticalLayout();
        HorizontalLayout lowerBlock = new HorizontalLayout();

        NormalTextField questionField = new NormalTextField();
        questionField.setPlaceholder(getTranslation("request_form.question_placeholder"));
        questionField.setValueChangeMode(ValueChangeMode.EAGER);
        questionField.setValue(question);

        Select<String> columnSelection = new Select<>();
        columnSelection.setItems(tableColumns);
        columnSelection.setPlaceholder(getTranslation("request_form.table_column_placeholder"));
        columnSelection.setValue(tableColumn);

        NormalButton removeBlockButton = new NormalButton(new Icon(VaadinIcon.MINUS), e -> remove(block));
        removeBlockButton.setText(getTranslation("request_form.remove_question_button"));
        upperBlock.add(questionField, columnSelection, removeBlockButton);

        NormalButton addAnswer = new NormalButton(new Icon(VaadinIcon.PLUS), e -> {
            HorizontalLayout answerLayout = new HorizontalLayout();
            SmallTextField answerField = new SmallTextField();
            answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
            answerField.setValueChangeMode(ValueChangeMode.EAGER);
            NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answerLayout));
            removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
            answerLayout.add(answerField, removeAnswer);
            middleBlock.addComponentAtIndex(middleBlock.getComponentCount() - 1, answerLayout);
        });
        addAnswer.setText(getTranslation("request_form.new_answer_button"));
        if (!answers.isEmpty()) {
            for (String answer : answers) {
                HorizontalLayout answerLayout = new HorizontalLayout();
                SmallTextField answerField = new SmallTextField();
                answerField.setPlaceholder(getTranslation("request_form.answer_placeholder"));
                answerField.setValueChangeMode(ValueChangeMode.EAGER);
                answerField.setValue(answer);
                NormalButton removeAnswer = new NormalButton(new Icon(VaadinIcon.MINUS), c -> middleBlock.remove(answerLayout));
                removeAnswer.setText(getTranslation("request_form.remove_answer_button"));
                answerLayout.add(answerField, removeAnswer);
                middleBlock.add(answerLayout);
            }
            middleBlock.add(addAnswer);
        } else {
            middleBlock.add(addAnswer);
            addAnswer.click();
        }

        ComboBox<String> customAnswerFormat = new ComboBox<>();
        customAnswerFormat.setPlaceholder(getTranslation("request_form.custom_answer_format_placeholder"));
        customAnswerFormat.setItems(Arrays.stream(Formats.values()).map(Formats::toString).toList());
        if (!customAnswer.isEmpty()) {
            customAnswerFormat.setValue(decodeFormat(customAnswer.get(0)));
        } else {
            customAnswerFormat.setValue(Formats.ANY.toString());
        }
        customAnswerFormat.setSizeFull();
        customAnswerFormat.setReadOnly(true);

        Checkbox customAnswerSelection = new Checkbox(getTranslation("request_form.custom_answer"));
        customAnswerSelection.addValueChangeListener(c -> customAnswerFormat.setReadOnly(!c.getValue()));
        customAnswerSelection.setSizeFull();
        customAnswerSelection.setValue(!customAnswer.isEmpty());
        lowerBlock.add(customAnswerSelection, customAnswerFormat);

        block.add(upperBlock, middleBlock, lowerBlock);
        block.setAlignItems(Alignment.START);
        block.setSizeFull();
        blocks.add(block);

        add(block);
    }

    public JSONObject edit() {
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
            Select<String> tableColumn = (Select<String>) upperBlock.getComponentAt(1);
            String columnValue = tableColumn.getValue();
            if (columnValue == null || columnValue.isEmpty() || columnValue.isBlank()) {
                ;
            } else {
                content.put(JSONKeys.TABLE_COLUMN.toString(), columnValue);
            }
            JSONObject wrapper = new JSONObject();
            wrapper.put(question.getValue(), content);
            json.put(String.valueOf(questionOrder++), wrapper);
        }
        if(json.isEmpty()) {
            return json;
        }
        json.put(JSONKeys.MULTIPLE_REGISTRATION.toString(), multipleRegistrationValue);
        return json;
    }

    private String decodeFormat(String format) {
        if (Formats.ANY.name().toLowerCase().equals(format)) {
            return Formats.ANY.toString();
        } else if (Formats.ONLY_LETTERS.name().toLowerCase().equals(format)) {
            return Formats.ONLY_LETTERS.toString();
        } else if (Formats.INTEGER.name().toLowerCase().equals(format)) {
            return Formats.INTEGER.toString();
        } else if (Formats.DATE.name().toLowerCase().equals(format)) {
            return Formats.DATE.toString();
        } else if (Formats.PERSON_NAME.name().toLowerCase().equals(format)) {
            return Formats.PERSON_NAME.toString();
        } else if (Formats.PHONE_NUMBER.name().toLowerCase().equals(format)) {
            return Formats.PHONE_NUMBER.toString();
        } else if (Formats.EMAIL_ADDRESS.name().toLowerCase().equals(format)) {
            return Formats.EMAIL_ADDRESS.toString();
        } else {
            return "Unknown Format";
        }
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

    private record QuestionRecord(String question, List<String> answers,
                                  List<String> customAnswer, String tableColumn) {}
}
