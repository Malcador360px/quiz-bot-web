package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.PasswordField;
import com.web.quiz_bot.configuration.Sizing;

public class NormalPasswordField extends PasswordField {

    public NormalPasswordField() {
        super();
        init();
    }

    public NormalPasswordField(String label) {
        super(label);
        init();
    }

    public NormalPasswordField(String label, String placeholder) {
        super(label, placeholder);
        init();
    }

    public NormalPasswordField(HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(listener);
        init();
    }

    public NormalPasswordField(String label,
                               HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, listener);
        init();
    }

    public NormalPasswordField(String label, String initialValue,
                               HasValue.ValueChangeListener
                                       <? super AbstractField.ComponentValueChangeEvent<PasswordField, String>> listener) {
        super(label, initialValue, listener);
        init();
    }

    private void init() {
        getStyle().set("font-size", Sizing.NORMAL_TEXT_FIELD_FONT_SIZE);
        setWidth(Sizing.NORMAL_TEXT_FIELD_WIDTH);
        setHeight(Sizing.NORMAL_TEXT_FIELD_HEIGHT);
    }
}
