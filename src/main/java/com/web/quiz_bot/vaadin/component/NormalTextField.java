package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import com.web.quiz_bot.configuration.Sizing;

public class NormalTextField extends TextField {

    public NormalTextField() {
        super();
        init();
    }

    public NormalTextField(String label) {
        super(label);
        init();
    }

    public NormalTextField(String label, String placeholder) {
        super(label, placeholder);
        init();
    }

    public NormalTextField(String label, String initialValue, String placeholder) {
        super(label, initialValue, placeholder);
        init();
    }

    public NormalTextField(HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(listener);
        init();
    }

    public NormalTextField(String label,
                           HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, listener);
        init();
    }

    public NormalTextField(String label, String initialValue,
                           HasValue.ValueChangeListener
                                   <? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        super(label, initialValue, listener);
        init();
    }

    private void init() {
        getStyle().set("font-size", Sizing.NORMAL_TEXT_FIELD_FONT_SIZE);
        setWidth(Sizing.NORMAL_TEXT_FIELD_WIDTH);
        setHeight(Sizing.NORMAL_TEXT_FIELD_HEIGHT);
    }
}
