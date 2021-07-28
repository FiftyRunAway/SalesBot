package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.database.MongoDB;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class NotificationsCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(NotificationsCommand.class.getName());

    public NotificationsCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        Long chatId = chat.getId();
        StringBuilder sb = new StringBuilder();
        if (UtilsDB.switchNotifications(chatId)) {
            boolean enabled = Boolean.parseBoolean(UtilsDB.getValue(MongoDB.getAppsCollection(), chatId).first()
                    .get("notifications").toString());
            sb.append(Icon.CHECK.get()).append(" Уведомления о скидках на отслеживаемые игры <b>")
                    .append(enabled ? "включены" : "отключены").append("</b>!");
        } else {
            sb.append("❗Добавьте, пожалуйста, SteamID игр, за которыми хотите следить!");
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,  sb.toString(), null, false);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
