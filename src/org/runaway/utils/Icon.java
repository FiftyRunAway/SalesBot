package org.runaway.utils;

import com.vdurmont.emoji.EmojiParser;

public enum Icon {
    MINUS(":heavy_minus_sign:"),
    CHECK(":white_check_mark:"),
    NOT(":x:"),
    UPDATE(":arrows_counterclockwise:");

    private String value;

    public String get() {
        return EmojiParser.parseToUnicode(value);
    }

    Icon(String value) {
        this.value = value;
    }
}
