package org.runaway.commands.main.cmds;

import org.runaway.commands.main.MainCommand;
import org.runaway.constructors.App;
import org.runaway.database.UtilsDB;
import org.runaway.utils.AppType;
import org.runaway.utils.Icon;
import org.runaway.utils.Keyboards;
import org.runaway.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PricesCommand extends MainCommand {
    private Logger logger = LoggerFactory.getLogger(PricesCommand.class.getName());

    public PricesCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);
        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                getText(chat.getId()), Keyboards.getUpdateKeyboard(), true);

        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }

    public static String getText(long user_id) {
        List<Integer> apps = UtilsDB.getUserApps(user_id);
        StringBuilder sb = new StringBuilder();
        AtomicInteger in = new AtomicInteger(1);
        if (apps != null) {
            try {
                apps.forEach(i -> {
                    App app = UtilsDB.toApp(i);
                    Date update = app.getPrice().getLastUpdate();
                    long milliseconds = new Date().getTime() - update.getTime();
                    if (milliseconds > 900000) {
                        app = UtilsDB.updatePrice(app);
                    }
                    boolean hasDisc = app.getPrice().isSale();
                    sb.append(hasDisc ? Icon.CHECK.get() : Icon.NOT.get()).append(" <a href=\"").append("https://store.steampowered.com/app/")
                            .append(i).append("/\">").append(app.getName()).append("</a> ")
                            .append(app.getPrice().getInitial_price() > 0 ? "(" + app.getPrice().getInitial_price() + " руб.) " : "")
                            .append(app.getPrice().isPreorder() ? "\n     <b>Предзаказ - <i>" + app.getReleaseDate() + "</i></b>" : "")
                            .append(hasDisc ? "\n     <b>Сейчас - " + app.getPrice().getFormated_price() +
                                    " (<i>-" + (int) app.getPrice().getDiscount() + "%</i>)</b>" : "").append("\n");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sb.append("❗Добавьте, пожалуйста, SteamID игр, за которыми хотите следить!");
        }
        return sb.toString();
    }
}
