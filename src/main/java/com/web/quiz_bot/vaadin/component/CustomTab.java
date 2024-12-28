package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.tabs.Tab;
import com.web.quiz_bot.configuration.Design;
import com.web.quiz_bot.configuration.Sizing;

@CssImport(value = "./styles/tab.css", themeFor = "vaadin-tab")
public class CustomTab extends Tab {

    public CustomTab() {
        super();
        init();
    }

    public CustomTab(String label) {
        super(label);
        init();
    }

    public CustomTab(Component... components) {
        super(components);
        init();
    }

    private void init() {
    }
}
