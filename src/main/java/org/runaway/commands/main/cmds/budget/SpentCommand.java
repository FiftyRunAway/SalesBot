package org.runaway.commands.main.cmds.budget;

import org.runaway.commands.main.MainCommand;
import org.runaway.commands.main.cmds.DonateCommand;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Invoices;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SpentCommand extends MainCommand {

    private Logger logger = LoggerFactory.getLogger(DonateCommand.class.getName());

    public SpentCommand(String identifier, String description) {
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
            if (strings.length == 1) {
                try {
                    int money = Integer.parseInt(strings[0]);
                    UtilsDB.spentMoney(user.getId(), money);
                    sb.append(Icon.MINUS.get()).append(" Вы потратили <b>").append(money).append(" руб.</b>");
                } catch (NumberFormatException e) {
                    sb.append(Icon.NOT.get()).append(" Ввести можно только числовое значение...");
                }
            } else {
                sb.append("Введите сумму после /spent, которую вы потратили");
            }

            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,  sb.toString(), null, false, Invoices.invoiceDonate(chatId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
