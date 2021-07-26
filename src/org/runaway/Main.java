package org.runaway;

import org.runaway.database.MongoDB;
import org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class.getName());
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
}
