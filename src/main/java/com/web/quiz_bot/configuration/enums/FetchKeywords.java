package com.web.quiz_bot.configuration.enums;

public enum FetchKeywords {
    INFO,
    QUIZ,
    CSV,
    EXCEL;

    @Override
    public String toString() {
        if (this == INFO) {
            return "info";
        } else if (this == QUIZ) {
            return "quiz";
        } else if (this == CSV) {
            return "csv";
        } else if (this == EXCEL) {
            return "excel";
        } else {
            return "";
        }
    }
}
