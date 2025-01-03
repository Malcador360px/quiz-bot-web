package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.vaadin.layout.OuterMenuLayout;

@Route(value = "")
@UIScope
@AnonymousAllowed
@PWA(
        name="quiz-bot",
        shortName="quiz-bot",
        offlinePath="offline.html"
)
public class MainView extends VerticalLayout implements AppShellConfigurator {

    public MainView() {
        addClassName("main-view");
    }
}
