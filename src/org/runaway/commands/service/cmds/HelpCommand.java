package org.runaway.commands.service.cmds;

import org.runaway.commands.service.ServiceCommand;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelpCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(HelpCommand.class.getName());

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "❗*Список команд*\n" +
                        "/prices - просмотреть актуальные цены\n" +
                        "/list - просмотреть текущие игры\n" +
                        "/add <SteamID> - добавить игру из Steam\n" +
                        "/remove - удалить игру из списка\n" +
                        "/help - помощь\n\n" +
                        "❗Если вы добавили неверный SteamID и бот сломался, удалите игру вручную\n" +
                        "Используйте: /remove <SteamID>", null);
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
