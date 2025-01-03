package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.RouterLayout;

@CssImport(value = "./styles/app-layout.css", themeFor = "vaadin-app-layout")
public class CustomAppLayout extends AppLayout implements RouterLayout {

    public CustomAppLayout() {
        super();
    }
}
