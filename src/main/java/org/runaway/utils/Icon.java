package org.runaway.utils;

import com.vdurmont.emoji.EmojiParser;

public enum Icon {
    MINUS(":heavy_minus_sign:"),
    CHECK(":white_check_mark:"),
    NOT(":x:"),
    UPDATE(":arrows_counterclockwise:"),
    ZERO(":zero:"),
    ONE(":one:"),
    TWO(":two:"),
    THREE(":three:"),
    FOUR(":four:"),
    FIVE(":five:"),
    SIX(":six:"),
    SEVEN(":seven:"),
    EIGHT(":eight:"),
    NINE(":nine:"),
    BELL(":bell:"),
    RAINBOW(":rainbow:"),
    RIGHT_ARROW(":arrow_right:");

    private String value;

    public String get() {
        return EmojiParser.parseToUnicode(value);
    }

    Icon(String value) {
        this.value = value;
    }

    public static String getNumberString(int number) {
        if (number >= 10) {
            return getNumber(number / 10) + getNumber(number % 10);
        }
        return getNumber(number);
    }

    private static String getNumber(int number) {
        switch (number) {
            case 0: {
                return ZERO.get();
            }
            case 1: {
                return ONE.get();
            }
            case 2: {
                return TWO.get();
            }
            case 3: {
                return THREE.get();
            }
            case 4: {
                return FOUR.get();
            }
            case 5: {
                return FIVE.get();
            }
            case 6: {
                return SIX.get();
            }
            case 7: {
                return SEVEN.get();
            }
            case 8: {
                return EIGHT.get();
            }
            case 9: {
                return NINE.get();
            }
        }
        return MINUS.get();
    }
}
