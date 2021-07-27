package org.runaway.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class ServiceCommand extends BotCommand {
    private Logger logger = LoggerFactory.getLogger(ServiceCommand.class.getName());

    public ServiceCommand(String identifier, String description) {
        super(identifier, description);
    }

    protected void sendAnswer(AbsSender absSender, Long chatId, String commandName, String username, String text, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        if (keyboard != null) message.setReplyMarkup(keyboard);
        try {
            absSender.execute(message);
        } catch (TelegramApiException exception) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s", exception.getMessage(), commandName, username));
            exception.printStackTrace();
        }
    }
}
