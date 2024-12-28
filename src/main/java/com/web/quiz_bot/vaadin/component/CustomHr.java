package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.html.Hr;

public class CustomHr extends Hr {

    public CustomHr() {
        super();
        init();
    }

    private void init() {
        getStyle().set("color", "#406E8E");
    }
}
