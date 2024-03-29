package org.runaway.steam;

import com.mongodb.Block;
import org.bson.Document;
import org.runaway.commands.main.MainCommand;
import org.runaway.constructors.App;
import org.runaway.database.MongoDB;
import org.runaway.database.UtilsDB;
import org.runaway.utils.Icon;
import org.runaway.utils.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Notifications {

    public static void start() throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        MongoDB.getAppIdsCollection().find().forEach((Block<? super Document>) document -> {
            list.add(Integer.parseInt(document.get("id").toString()));
        });

        new SteamRunnable().updateAll(list);
        SteamRunnable.oldApps = new HashMap<>();
        SteamRunnable.newApps = new HashMap<>();

        Thread.sleep(30000);
        HashMap<Integer, App> oldApps = SteamRunnable.getOldApps();
        HashMap<Integer, App> newApps = SteamRunnable.getNewApps();

        MongoDB.getAppsCollection().find().forEach((Block<? super Document>) document -> {
            long user_id = document.getLong("id");
            if (UtilsDB.notifEnabled(user_id)) {
                List<Integer> i = UtilsDB.getUserApps(user_id);
                if (i != null) {
                    StringBuilder sb = new StringBuilder(Icon.BELL.get() + " Новые скидки!\n\n")
                            .append("Изменения:\n");
                    boolean hasInfo = false;
                    for (Integer integer : i) {
                        App oldA = oldApps.get(integer);
                        App newA = newApps.get(integer);

                        if (oldA.getPrice().getDiscount() < newA.getPrice().getDiscount()) {
                            String old = oldA.getPrice().getFormated_price();
                            sb.append(Icon.CHECK.get()).append(" <b>")
                                    .append(oldA.getName()).append("</b> ")
                                    .append(old == null ? "Предзаказ" : old).append(" ").append(Icon.RIGHT_ARROW.get()).append(" ")
                                    .append(newA.getPrice().getFormated_price()).append("\n");
                            hasInfo = true;
                        }
                        if (!oldA.getReleaseDate().equals(newA.getReleaseDate())) {
                            String old = oldA.getReleaseDate();
                            sb.append(Icon.DATE.get()).append(" <b>")
                                    .append(oldA.getName()).append("</b> ")
                                    .append(old).append(" ").append(Icon.RIGHT_ARROW.get()).append(" ")
                                    .append(newA.getReleaseDate()).append("\n");
                            hasInfo = true;
                        }
                        if (oldA.getPrice().isPreorder() && !newA.getPrice().isPreorder()) {
                            sb.append(Icon.GAMEPAD.get()).append(" Только что вышла игра <b>")
                                    .append(oldA.getName()).append("</b>, поспешите купить её! (<b>")
                                    .append(newA.getPrice().getFormated_price()).append("</b>)");
                            hasInfo = true;
                        }
                    }
                    try {
                        if (hasInfo) Utils.sendMessage(user_id, sb.toString());
                    } catch (UnsupportedEncodingException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
