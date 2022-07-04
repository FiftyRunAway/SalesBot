package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.utils.Invoices;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class DonateCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(DonateCommand.class.getName());

    public DonateCommand(String identifier, String description) {
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
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,  sb.toString(), null, false, Invoices.invoiceDonate(chatId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
