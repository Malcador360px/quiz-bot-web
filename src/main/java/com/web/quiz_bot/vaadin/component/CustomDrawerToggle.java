package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.web.quiz_bot.configuration.Sizing;

public class CustomDrawerToggle extends DrawerToggle {

    public CustomDrawerToggle() {
        super();
        init();
    }

    private void init() {
        getStyle().set("font-size", Sizing.DRAWER_TOGGLE_FONT_SIZE);
        setWidth(Sizing.DRAWER_TOGGLE_WIDTH);
        setHeight(Sizing.DRAWER_TOGGLE_HEIGHT);
    }
}
