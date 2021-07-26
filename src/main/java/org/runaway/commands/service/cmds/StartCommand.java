package main.java.org.runaway.commands.service.cmds;

import main.java.org.runaway.utils.Utils;
import main.java.org.runaway.commands.service.ServiceCommand;
import main.java.org.runaway.database.UtilsDB;
import main.java.org.runaway.utils.Keyboards;
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
                "Добро пожаловать, " + user_first_name + "! Это бот, который следит за скидками игр " +
                        "в Steam! Просто нажмите кнопку '/add' внизу, " +
                        "чтобы начать добавление игр. Другие команды можно посмотреть в '/help'\n\n" +
                        "❗Важная информация: \n" +
                        "1. Если вы добавили неверный SteamID и бот сломался, удалите SteamID вручную\n" +
                        "    Используйте: /remove <SteamID>\n" +
                        "2. Пока нет", Keyboards.getMainKeyboard());
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}
