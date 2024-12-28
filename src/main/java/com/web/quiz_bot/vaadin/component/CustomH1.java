package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.web.quiz_bot.configuration.Design;
import com.web.quiz_bot.configuration.Sizing;

public class CustomH1 extends H1 {

    public CustomH1() {
        super();
        init();
    }

    public CustomH1(Component... components) {
        super(components);
        init();
    }

    public CustomH1(String text) {
        super(text);
        init();
    }

    private void init(){
        getStyle().set("color", "#406E8E");
        getStyle().set("font-family", Design.DEFAULT_FONT);
        getStyle().set("font-size", Sizing.H1_FONT_SIZE);
        setWidth(Sizing.H1_WIDTH);
        setHeight(Sizing.H1_HEIGHT);
    }
}
