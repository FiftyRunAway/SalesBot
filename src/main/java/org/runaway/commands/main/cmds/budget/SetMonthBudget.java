package org.runaway.commands.main.cmds.budget;

import org.runaway.commands.main.MainCommand;
import org.runaway.commands.main.cmds.AddAppCommand;
import org.runaway.constructors.App;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class SetMonthBudget extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(AddAppCommand.class.getName());

    public SetMonthBudget(String identifier, String description) {
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
                int money = Integer.parseInt(strings[0]);
                UtilsDB.setNewMonthlyBudget(user.getId(), money);
                sb.append(Icon.CHECK.get()).append(" Вы успешно установили новый бюджет на этот месяц - <b>").append(money).append(" руб.</b>");
            } catch (NumberFormatException e) {
                sb.append(Icon.NOT.get()).append(" Ввести можно только числовое значение...");
            }
        } else {
            sb.append("<b>Введите сумму, которую собираетесь потратить в этом месяце</b>");
        }
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, sb.toString(), null, false);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
