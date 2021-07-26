package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Keyboards;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class RemoveAppCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(RemoveAppCommand.class.getName());

    public RemoveAppCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        Long chatId = chat.getId();
        StringBuilder sb = new StringBuilder();
        try {
            InlineKeyboardMarkup keyboardMarkup = null;
            if (strings.length == 1) {
                try {
                    int id = Integer.parseInt(strings[0]);
                    if (UtilsDB.removeFromUser(user.getId(), id)) {
                        sb.append(Icon.CHECK.get()).append(" Игра со SteamID '").append(id)
                                .append("' успешно удалена!\nОбновите список игр");
                    } else sb.append("❗Игра с таким SteamID не может быть удалена!");
                } catch (NumberFormatException exception) {
                    sb.append(Icon.NOT.get()).append(" Ввести можно только числовое значение!");
                }
            } else {
                List<Integer> s = UtilsDB.getUserApps(chatId);
                if (s != null && !s.isEmpty()) {
                    keyboardMarkup = Keyboards.getRemoveKeyboard(user.getId());
                    sb.append("Выберите те игры, которые хотите удалить");
                } else {
                    sb.append("❗Так не выйдет! Сначала добавьте игру\n")
                    .append("Используйте так:\n/add [SteamID] - добавить игру из Steam");
                }
            }
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, sb.toString(), keyboardMarkup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
