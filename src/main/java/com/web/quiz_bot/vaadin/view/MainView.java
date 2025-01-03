package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;

@Route(value = "", layout = OuterMenuLayout.class)
@UIScope
@AnonymousAllowed
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("main-view");
    }
}
