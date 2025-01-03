package com.web.quiz_bot.servlet;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = "/*", name = "slot", asyncSupported = true, initParams = {
        @WebInitParam(name = Constants.I18N_PROVIDER, value = "com.web.quiz_bot.localization.LocalizationProvider")})
@PWA(
        name="quiz-bot",
        shortName="quiz-bot",
        offlinePath = "offline.html"
)
public class MainServlet extends VaadinServlet implements AppShellConfigurator {
}
