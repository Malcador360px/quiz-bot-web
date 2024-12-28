package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.web.quiz_bot.configuration.Sizing;

@CssImport(value = "./styles/normal-button.css", themeFor = "vaadin-button")
public class NormalButton extends Button {

    public NormalButton() {
        super();
        init();
    }

    public NormalButton(String text) {
        super(text);
        init();
    }

    public NormalButton(Component icon) {
        super(icon);
        init();
    }

    public NormalButton(String text, Component icon) {
        super(text, icon);
        init();
    }

    public NormalButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        init();
    }

    public NormalButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        init();
    }

    public NormalButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        init();
    }

    private void init() {
        addThemeName("normal-button");
        getStyle().set("font-size", Sizing.NORMAL_BUTTON_FONT_SIZE);
        setWidth(Sizing.NORMAL_BUTTON_WIDTH);
        setHeight(Sizing.NORMAL_BUTTON_HEIGHT);
    }
}
