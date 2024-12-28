package com.web.quiz_bot.vaadin.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.web.quiz_bot.service.UserService;
import com.web.quiz_bot.util.VaadinViewUtil;
import com.web.quiz_bot.vaadin.layout.InnerMenuLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "dashboard", layout = InnerMenuLayout.class)
@PreserveOnRefresh
@UIScope
@PermitAll
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    @Autowired
    public DashboardView(UserService userService) {
        this.userService = userService;
        addClassName("dashboard-view");
    }

    private void createCell() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.setEnabled(VaadinViewUtil.getCurrentUser(userService).isVerified());
    }
}
