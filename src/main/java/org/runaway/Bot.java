package main.java.org.runaway;

import main.java.org.runaway.commands.service.cmds.StartCommand;
import main.java.org.runaway.utils.Icon;
import main.java.org.runaway.utils.Keyboards;
import main.java.org.runaway.commands.main.MainCommand;
import main.java.org.runaway.commands.main.cmds.AddAppCommand;
import main.java.org.runaway.commands.main.cmds.ListCommand;
import main.java.org.runaway.commands.main.cmds.PricesCommand;
import main.java.org.runaway.commands.main.cmds.RemoveAppCommand;
import main.java.org.runaway.commands.service.cmds.HelpCommand;
import main.java.org.runaway.database.MongoDB;
import main.java.org.runaway.database.UtilsDB;
import main.java.org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingCommandBot {
    private static Logger logger = LoggerFactory.getLogger(Bot.class.getName());
    public static MongoDB mongoDB;

    public static void main(String[] args) {
        //Подключение к ДБ
        mongoDB = new MongoDB();

        //Регистрация телеграм бота
        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot(Vars.BOT_NAME.getString()));
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
        logger.info("Бот успешно запущен!");
    }

    public Bot(String botUsername) {
        super(botUsername);

        register(new StartCommand("start", "Стартовая команда"));
        register(new HelpCommand("help", "Помощь"));
        register(new ListCommand("list", "Список ваших SteamID"));
        register(new AddAppCommand("add", "Добавить Steam ID в список"));
        register(new RemoveAppCommand("remove", "Удалить Steam ID из списка"));
        register(new PricesCommand("prices", "Показать актуальные цены"));

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(String.valueOf(message.getChatId()));
            commandUnknownMessage.setText("❗'" + message.getText() + "' неизвестна боту. " +
                    "Напишите \"/help\" для большей информации");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(String.valueOf(message.getChatId()));
                echoMessage.setText("Вы, если я не ошибаюсь, написали:\n" + message.getText());

                try {
                    execute(echoMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long user_id = callbackQuery.getMessage().getChatId();

            if (callbackQuery.getData().equals("update")) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(String.valueOf(user_id));
                editMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessage.setReplyMarkup(Keyboards.getUpdateKeyboard());
                editMessage.enableHtml(true);
                editMessage.disableWebPagePreview();
                editMessage.setText(MainCommand.messageFormat(PricesCommand.getText(user_id), true));
                try {
                    execute(editMessage);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            } else if (callbackQuery.getData().split(" ")[0].equals("remove")) {
                int app_id = Integer.parseInt(callbackQuery.getData().split(" ")[1]);

                StringBuilder sb = new StringBuilder();
                if (UtilsDB.removeFromUser(user_id, app_id)) {
                    sb.append(Icon.CHECK.get()).append(" Игра со SteamID \"").append(app_id).append("\" успешно удалена!\nОбновите список игр");
                } else {
                    sb.append("❗ Игра с таким SteamID не может быть удалена!");
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(user_id));
                sendMessage.setText(sb.toString());
                sendMessage.disableWebPagePreview();
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotToken() {
        return Vars.BOT_TOKEN.getString();
    }
}
