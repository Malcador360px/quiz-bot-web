package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.web.quiz_bot.configuration.Sizing;

public class MenuHorizontal extends HorizontalLayout {

    public MenuHorizontal() {
        super();
        setSize();
    }

    public MenuHorizontal(Component... children) {
        super(children);
        setSize();
    }

    private void setSize() {
        setWidth(Sizing.MENU_HORIZONTAL_WIDTH);
        setHeight(Sizing.MENU_HORIZONTAL_HEIGHT);
    }
}
