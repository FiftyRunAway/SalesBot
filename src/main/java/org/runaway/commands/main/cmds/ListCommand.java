package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.database.MongoDB;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ListCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(ListCommand.class.getName());

    public ListCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        Long chatId = chat.getId();
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        List<Integer> appids = UtilsDB.getUserApps(user.getId());
        if (appids != null && !appids.isEmpty()) {
            sb.append("Игры, за которыми вы следите\n\n");
            appids.forEach(in -> {
                UtilsDB.checkSteamID(in);
                sb.append(i.getAndIncrement()).append(". ").append(UtilsDB.getValue(MongoDB.getAppIdsCollection(), in).first().get("name")).append(" (<b>")
                        .append(in).append("</b>)\n");
            });
        } else {
            sb.append("❗ Добавьте, пожалуйста, SteamID игр, за которыми хотите следить!");
        }
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,  sb.toString(), null, false);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
