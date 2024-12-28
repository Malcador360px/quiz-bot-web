package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;

@Route(value = "about-us", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
public class AboutUsView extends VerticalLayout {

    public AboutUsView() {
        addClassName("about-us-view");
    }
}
