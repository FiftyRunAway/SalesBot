package org.runaway.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public enum Vars {
    BOT_NAME("steampricesbot"),
    BOT_TOKEN("1906394212:AAEC14ctpjbs9dXZ4cdfEIO10wEwg5DJ-wk"),
    DB_PASSWORD("q3L15r4lNh22oEYZ"),
    DB_USERNAME("runaway222"),
    DB_CLUSTER("Cluster"),
    DB_DATABASE("TelegramBot"),
    DB_COLLECTION_USERS("users"),
    DB_COLLECTION_APPIDS("appids"),
    DB_COLLECTION_APPS("apps"),
    DB_COLLECTION_BUDGET("budget");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d MM yyyy HH:mm:ss", Locale.forLanguageTag("ru"));
    private static final SimpleDateFormat dateFormatBeautiful = new SimpleDateFormat("d MMM HH:mm", Locale.forLanguageTag("ru"));
    private String string;

    Vars(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static SimpleDateFormat getDateFormatBeautiful() {
        return dateFormatBeautiful;
    }
}
