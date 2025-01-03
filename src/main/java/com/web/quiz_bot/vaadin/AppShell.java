package com.web.quiz_bot.vaadin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

@PWA(
        name="quiz-bot",
        shortName="quiz-bot",
        offlinePath = "offline.html"
)
public class AppShell implements AppShellConfigurator {

}
