package org.runaway.utils;

 import org.runaway.database.MongoDB;
import org.runaway.database.UtilsDB;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Keyboards {

    private static final String callbackOnUpdate = "update";
    ReplyKeyboard keyboard;

    Keyboards(ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public static ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        firstRow.add(KeyboardButton.builder().text("/prices").build());
        keyboard.add(firstRow);
        secondRow.add(KeyboardButton.builder().text("/add").build());
        secondRow.add(KeyboardButton.builder().text("/list").build());
        secondRow.add(KeyboardButton.builder().text("/help").build());
        secondRow.add(KeyboardButton.builder().text("/remove").build());
        keyboard.add(secondRow);
        thirdRow.add(KeyboardButton.builder().text("/notify").build());
        keyboard.add(thirdRow);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getUpdateKeyboard() {
        InlineKeyboardMarkup k = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> btns = new ArrayList<>();
        btns.add(InlineKeyboardButton.builder().text("Обновить цены " + Icon.UPDATE.get())
                .callbackData(callbackOnUpdate).build());
        rows.add(btns);
        k.setKeyboard(rows);
        return k;
    }

    public static InlineKeyboardMarkup getRemoveKeyboard(long user_id) {
        InlineKeyboardMarkup k = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> first = new ArrayList<>(),
                second = new ArrayList<>(),
                third = new ArrayList<>(),
                fourth = new ArrayList<>(),
                fifth = new ArrayList<>(),
                sixth = new ArrayList<>();
        AtomicInteger in = new AtomicInteger();
        List<Integer> appids = UtilsDB.getUserApps(user_id);
        if (appids != null && !appids.isEmpty()) {
            appids.forEach(i -> {
                UtilsDB.checkSteamID(i);
                String name = UtilsDB.getValue(MongoDB.getAppIdsCollection(), i).first().get("name").toString();

                InlineKeyboardButton btn = InlineKeyboardButton.builder()
                        .callbackData("remove " + i).text(name).build();
                if (in.get() < 3) {
                    first.add(btn);
                } else if (in.get() > 2 && in.get() < 6) {
                    second.add(btn);
                } else if (in.get() > 5 && in.get() < 9) {
                    third.add(btn);
                } else if (in.get() > 8 && in.get() < 12) {
                    fourth.add(btn);
                } else if (in.get() > 11 && in.get() < 15) {
                    fifth.add(btn);
                } else if (in.get() > 14 && in.get() < 18) {
                    sixth.add(btn);
                }
                in.incrementAndGet();
            });
        } else {
            return k;
        }
        rows.add(first);
        if (!second.isEmpty()) rows.add(second);
        if (!third.isEmpty()) rows.add(third);
        if (!fourth.isEmpty()) rows.add(fourth);
        if (!fifth.isEmpty()) rows.add(fifth);
        if (!sixth.isEmpty()) rows.add(sixth);
        k.setKeyboard(rows);
        return k;
    }
}
