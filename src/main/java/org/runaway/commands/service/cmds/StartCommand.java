package org.runaway.commands.service.cmds;

import org.runaway.commands.service.ServiceCommand;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Keyboards;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(StartCommand.class.getName());

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        String user_first_name = chat.getFirstName();
        String user_last_name = chat.getLastName();
        UtilsDB.registerUser(chat.getId(), userName, user_first_name, user_last_name);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                Icon.RAINBOW.get() + " Добро пожаловать, <b>" + user_first_name + "</b>! \n\nВы попали в бота, который следит за скидками игр " +
                        "в Steam! Просто нажмите кнопку '/add' внизу, " +
                        "чтобы начать добавление SteamID. Другие команды можно посмотреть в '/help'\n\n" +
                        Icon.BELL.get() + "Важная информация: \n" +
                        Icon.ONE.get() + " Если вы добавили неверный SteamID и бот сломался, удалите SteamID вручную.\n" +
                        "❗Используйте так:\n<b>/remove [SteamID]</b> - удалить SteamID из списка", Keyboards.getMainKeyboard());
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
