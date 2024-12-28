package com.web.quiz_bot.vaadin.component;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.web.quiz_bot.configuration.Sizing;

public class CustomTabs extends Tabs {

    public CustomTabs() {
        super();
        init();
    }

    public CustomTabs(Tab... tabs) {
        super(tabs);
        init();
    }

    public CustomTabs(boolean autoselect, Tab... tabs) {
        super(autoselect, tabs);
        init();
    }

    private void init() {
        setWidth(Sizing.TABS_WIDTH);
        setHeight(Sizing.TABS_HEIGHT);
    }
}
