package org.runaway.commands.main;

import org.runaway.utils.Icon;
import org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Date;

public abstract class MainCommand extends BotCommand {
    private Logger logger = LoggerFactory.getLogger(MainCommand.class.getName());

    public MainCommand(String identifier, String description) {
        super(identifier, description);
    }

    public void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text, ReplyKeyboard keyboard, boolean header) {
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        message.setChatId(chatId.toString());
        message.disableWebPagePreview();
        if (keyboard != null) message.setReplyMarkup(keyboard);
        message.setText(messageFormat(text, header));
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }

    public static String messageFormat(String text, boolean header) {
        return header ? Icon.UPDATE.get() + " Скидки на выбранные игры Steam (на " + Vars.getDateFormatBeautiful().format(addHoursToJavaUtilDate(new Date(), 3)) + ")\n\n" + text : text;
    }

    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
