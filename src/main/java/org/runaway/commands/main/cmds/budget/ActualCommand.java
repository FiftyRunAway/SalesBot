package org.runaway.commands.main.cmds.budget;

import org.bson.Document;
import org.runaway.commands.main.MainCommand;
import org.runaway.commands.main.cmds.AddAppCommand;
import org.runaway.database.MongoDB;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.crypto.Data;
import java.util.Calendar;
import java.util.Date;

public class ActualCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(AddAppCommand.class.getName());

    public ActualCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        Long chatId = chat.getId();
        StringBuilder sb = new StringBuilder();

        if (!UtilsDB.docExists(MongoDB.getBudgetCollection(), user.getId())) {
            sb.append("Сначала установите бюджет до конца календарного месяца!");
        } else {
            Document d = UtilsDB.getValue(MongoDB.getBudgetCollection(), user.getId()).first();
            long moneyLeft = d.getLong("budget") - d.getLong("spent");
            long daysLeft = (long)Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) - (long)new Date().getDay();

            sb.append(Icon.DATE.get()).append(" Ваш бюджет на оставшиеся ").append(daysLeft)
                    .append(" дн. - <b>").append(moneyLeft).append(" руб.</b>")
                    .append("\n\n").append(Icon.RIGHT_ARROW).append("Вы можете сегодня потратить - <b>")
                    .append(moneyLeft / daysLeft).append(" руб.</b>");
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, sb.toString(), null, false);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
