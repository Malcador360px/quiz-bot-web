package com.web.quiz_bot.vaadin.view.form.event;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public abstract class FormEvent extends ComponentEvent<Component> {

    protected FormEvent(Component source) {
        super(source, false);
    }
}
