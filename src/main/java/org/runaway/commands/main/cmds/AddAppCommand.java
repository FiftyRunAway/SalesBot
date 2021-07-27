package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.commands.service.ServiceCommand;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class AddAppCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(AddAppCommand.class.getName());

    public AddAppCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        Long chatId = chat.getId();
        StringBuilder sb = new StringBuilder();
        if (strings.length == 1) {
            try {
                int id = Integer.parseInt(strings[0]);
                List<Integer> l = UtilsDB.getUserApps(user.getId());
                if (l != null && l.contains(id)) {
                    sb.append(Icon.NOT.get()).append(" Такой SteamID уже добавлен...");
                } else if (l == null || !l.contains(id)) {
                    UtilsDB.addToUser(user.getId(), id);
                    UtilsDB.saveSteamID(id);
                    sb.append(Icon.CHECK.get()).append(" Игра со SteamID '<b>").append(id).append("</b>' успешно добавлена!\n\n" + Icon.UPDATE.get() + " Обновите список игр");
                }
            } catch (NumberFormatException e) {
                sb.append(Icon.NOT.get()).append(" Ввести можно только числовое значение...");
            }
        } else {
            sb.append("<b>Где взять SteamID?</b>\n")
                    .append(Icon.ONE.get()).append(" Сайт <a href=\"https://store.steampowered.com/\">Steam</a>\n")
                    .append(Icon.TWO.get()).append(" Сайт <a href=\"https://steamdb.info/\">SteamDB</a> (")
                    .append(Icon.CHECK.get()).append(" <b>рекомендую</b>)\n\n")
                    .append("❗Используйте так:\n<b>/add [SteamID]</b> - добавить игру из Steam");
        }
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, sb.toString(), null, false);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
