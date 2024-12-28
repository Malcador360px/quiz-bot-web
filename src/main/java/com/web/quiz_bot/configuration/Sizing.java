package com.web.quiz_bot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import javax.annotation.PostConstruct;

@Configuration
@PropertySource({"classpath:sizing.properties"})
public class Sizing {

    private final Environment environment;
    public static String MENU_HORIZONTAL_WIDTH;
    public static String MENU_HORIZONTAL_HEIGHT;
    public static String TABS_WIDTH;
    public static String TABS_HEIGHT;
    public static String TAB_FONT_SIZE;
    public static String DRAWER_TOGGLE_WIDTH;
    public static String DRAWER_TOGGLE_HEIGHT;
    public static String DRAWER_TOGGLE_FONT_SIZE;
    public static String H1_WIDTH;
    public static String H1_HEIGHT;
    public static String H1_FONT_SIZE;
    public static String NORMAL_BUTTON_WIDTH;
    public static String NORMAL_BUTTON_HEIGHT;
    public static String NORMAL_BUTTON_FONT_SIZE;
    public static String LANGUAGE_SELECT_WIDTH;
    public static String LANGUAGE_SELECT_HEIGHT;
    public static String NORMAL_TEXT_FIELD_WIDTH;
    public static String NORMAL_TEXT_FIELD_HEIGHT;
    public static String NORMAL_TEXT_FIELD_FONT_SIZE;
    public static String SMALL_TEXT_FIELD_WIDTH;
    public static String SMALL_TEXT_FIELD_HEIGHT;
    public static String SMALL_TEXT_FIELD_FONT_SIZE;

    @Autowired
    public Sizing(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void setSizing() {
        MENU_HORIZONTAL_WIDTH = environment.getProperty("menu_horizontal_width");
        MENU_HORIZONTAL_HEIGHT = environment.getProperty("menu_horizontal_height");
        TABS_WIDTH = environment.getProperty("tabs_width");
        TABS_HEIGHT = environment.getProperty("tabs_height");
        TAB_FONT_SIZE = environment.getProperty("tab_font_size");
        DRAWER_TOGGLE_WIDTH = environment.getProperty("drawer_toggle_width");
        DRAWER_TOGGLE_HEIGHT = environment.getProperty("drawer_toggle_height");
        DRAWER_TOGGLE_FONT_SIZE = environment.getProperty("drawer_toggle_font_size");
        H1_WIDTH = environment.getProperty("h1_width");
        H1_HEIGHT = environment.getProperty("h1_height");
        H1_FONT_SIZE = environment.getProperty("h1_font_size");
        NORMAL_BUTTON_WIDTH = environment.getProperty("normal_button_width");
        NORMAL_BUTTON_HEIGHT = environment.getProperty("normal_button_height");
        NORMAL_BUTTON_FONT_SIZE = environment.getProperty("normal_button_font_size");
        LANGUAGE_SELECT_WIDTH = environment.getProperty("language_select_width");
        LANGUAGE_SELECT_HEIGHT = environment.getProperty("language_select_height");
        NORMAL_TEXT_FIELD_WIDTH = environment.getProperty("normal_text_field_width");
        NORMAL_TEXT_FIELD_HEIGHT = environment.getProperty("normal_text_field_height");
        NORMAL_TEXT_FIELD_FONT_SIZE = environment.getProperty("normal_text_field_font_size");
        SMALL_TEXT_FIELD_WIDTH = environment.getProperty("small_text_field_width");
        SMALL_TEXT_FIELD_HEIGHT = environment.getProperty("small_text_field_height");
        SMALL_TEXT_FIELD_FONT_SIZE = environment.getProperty("small_text_field_font_size");
    }
}
