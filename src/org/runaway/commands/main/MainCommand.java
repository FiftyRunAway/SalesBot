package org.runaway.commands.main;

import org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        return header ? "Скидки на ваши игры (на " + Vars.getDateFormatBeautiful().format(new Date()) + ")\n\n" + text : text;
    }
}
