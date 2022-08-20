package org.runaway.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.runaway.constructors.App;
import org.runaway.constructors.Price;
import org.runaway.constructors.User;
import org.runaway.steam.Steam;
import org.runaway.utils.AppType;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtilsDB {
    private static Logger logger = LoggerFactory.getLogger(MongoDB.class.getName());

    /**
     * Заносит данные нового пользователя в MongoDB
     *
     */
    public static void registerUser(User user) {
        if (!docExists(MongoDB.getUsersCollection(), user.getUserId())) {
            MongoDB.getUsersCollection().insertOne(userDoc(user));
            logger.debug(String.format("Пользователь %s. Зарегистрирован в DB users!", user.getUsername()));
        }
    }

    /**
     * Добавить пользователю steamID в список
     * @param user_id
     * @param steamId
     */
    public static void addToUser(long user_id, int steamId) {
        List<Integer> apps = new ArrayList<>();
        apps.add(steamId);
        if (docExists(MongoDB.getAppsCollection(), user_id)) {
            Object o = getUserApps(user_id);
            if (o != null) {
                apps.addAll(Utils.stringToList(o.toString()));
            }
            boolean notifications = (Boolean) getValue(MongoDB.getAppsCollection(), user_id).first()
                    .get("notifications");
            MongoDB.getAppsCollection().replaceOne(new BasicDBObject("id", user_id),
                    new Document("id", user_id)
            .append("apps", apps.toString())
            .append("notifications", notifications));
        } else {
            Document doc = new Document("id", user_id)
                    .append("apps", apps.toString())
                    .append("notifications", false);
            MongoDB.getAppsCollection().insertOne(doc);
        }
    }

    /**
     * Удаляет у user_id steamId из списка
     * @param user_id
     * @param steamId
     * @return
     */
    public static boolean removeFromUser(long user_id, int steamId) {
        List<Integer> apps = new ArrayList<>();
        if (docExists(MongoDB.getAppsCollection(), user_id)) {
            Object o = getUserApps(user_id);
            if (o != null) {
                apps.addAll(Utils.stringToList(o.toString()));
            }
            if (apps.contains(steamId)) {
                apps.remove((Object) steamId);
                boolean notifications = (Boolean) getValue(MongoDB.getAppsCollection(), user_id).first()
                        .get("notifications");
                MongoDB.getAppsCollection().replaceOne(new BasicDBObject("id", user_id),
                        new Document("id", user_id)
                                .append("apps", apps.isEmpty() ? null : apps.toString())
                                .append("notifications", notifications));

                // Удаление из общего списка SteamID (чтобы не забивать бесплатную память MongoDB)
                removeSteamID(steamId);
                return true;
            }
        }
        return false;
    }

    /**
     * Удалить SteamID из appids коллекции
     */
    private static void removeSteamID(int steamId) {
        if (docExists(MongoDB.getAppIdsCollection(), steamId)) {
            MongoDB.getAppIdsCollection().deleteOne(new BasicDBObject("id", steamId));
        }
    }

    /**
     *
     * @param steamId игры, которая будет занесена в MongoDB
     */
    public static App saveSteamID(int steamId) {
        if (!isAppInBase(steamId)) {
            App app = Steam.getApp(steamId);
            MongoDB.getAppIdsCollection().insertOne(appDoc(app));
            logger.debug(String.format("Сохранена новый Steam ID в DB: %s", steamId));
            return app;
        }
        return null;
    }

    public static boolean checkSteamID(int steamId) {
        if (!isAppInBase(steamId)) {
            saveSteamID(steamId);
            return false;
        }
        return true;
    }

    /**
     *
     * @param steamId
     * @return App с данными из MongoDB
     */
    public static App toApp(int steamId) {
        App check = saveSteamID(steamId);
        if (check != null) return check;
        Document d = getValue(MongoDB.getAppIdsCollection(), steamId).first();

        String name = d.get("name").toString();
        AppType type = AppType.valueOf(d.get("type").toString());
        String release = d.get("release_date").toString();
        String formated_price = d.get("formated_price") == null ? "" : d.get("formated_price").toString();
        double discount = Double.parseDouble(d.get("discount").toString());
        double initial_price = Double.parseDouble(d.get("initial_price").toString());
        double final_price = Double.parseDouble(d.get("final_price").toString());
        Date last_update = (Date) Utils.stringToDate(Vars.getDateFormat(), d.get("last_update").toString()).clone();

        Price price = new Price(type == AppType.GAME, formated_price, discount, initial_price, final_price, last_update);

        return new App(steamId, name, type, price, release);
    }

    /**
     *
     * @param app чья цена обновится в MongoDB
     */
    public static App updatePrice(App app) {
        if (docExists(MongoDB.getAppIdsCollection(), app.getSteamId())) {
            Price newPrice = Steam.getOnlyPrice(app.getSteamId(), app.getAppType() == AppType.GAME);

            App newApp = new App(app.getSteamId(), app.getName(), newPrice.isPreorder() ? AppType.PREORDER : AppType.GAME,
                    new Price(app.getAppType() == AppType.GAME, newPrice.getFormated_price(),
                            newPrice.getDiscount(), newPrice.getInitial_price(), newPrice.getFinal_price(), new Date()), app.getReleaseDate());

            MongoDB.getAppIdsCollection().replaceOne(new BasicDBObject("id", app.getSteamId()),
                    appDoc(newApp));

            return newApp;
        }
        return null;
    }

    public static boolean notifEnabled(long user_id) {
        return (Boolean) getValue(MongoDB.getAppsCollection(), user_id).first().get("notifications");
    }

    public static boolean switchNotifications(long user_id) {
        boolean enabled;
        if (docExists(MongoDB.getAppsCollection(), user_id)) {
            Document d = getValue(MongoDB.getAppsCollection(), user_id).first();
            Object o = d.get("notifications");
            enabled = o != null && Boolean.parseBoolean(o.toString());
            List<Integer> apps = getUserApps(user_id);
            MongoDB.getAppsCollection().replaceOne(new BasicDBObject("id", user_id),
                    new Document("id", user_id)
            .append("apps", apps == null ? "[]" : apps.toString())
            .append("notifications", !enabled));
            return true;
        }
        return false;
    }

    public static void setNewMonthlyBudget(long user_id, long money) {
        if (docExists(MongoDB.getBudgetCollection(), user_id)) {
            MongoDB.getBudgetCollection().replaceOne(new BasicDBObject("id", user_id),
                    new Document("id", user_id)
                            .append("budget", money)
                            .append("month", new Date().getMonth())
                            .append("spent", 0));
        } else {
            MongoDB.getBudgetCollection().insertOne(
                    new Document("id", user_id)
                            .append("budget", money)
                            .append("month", new Date().getMonth())
                    .append("spent", 0));
        }
    }

    public static boolean spentMoney(long user_id, long money) {
        if (docExists(MongoDB.getBudgetCollection(), user_id)) {
            Document d = getValue(MongoDB.getBudgetCollection(), user_id).first();
            MongoDB.getBudgetCollection().replaceOne(new BasicDBObject("id", user_id),
                    new Document("id", user_id)
                            .append("budget", d.getLong("budget"))
                            .append("month", d.getLong("month"))
                            .append("spent", (d.getLong("spent") + money)));
            return true;
        }
        return false;
    }

    /**
     *
     * @param steam_id
     * @return находится этот Steam ID в базе MongoDB
     */
    public static boolean isAppInBase(int steam_id) {
        return docExists(MongoDB.getAppIdsCollection(), steam_id);
    }

    /**
     *
     * @param app
     * @return Document MongoDB для класса App
     */
    private static Document appDoc(App app) {
        return new Document("id", app.getSteamId())
                .append("name", app.getName())
                .append("type", app.getAppType().name())
                .append("release_date", app.getReleaseDate())
                .append("formated_price", app.getPrice().getFormated_price())
                .append("final_price", app.getPrice().getFinal_price())
                .append("initial_price", app.getPrice().getInitial_price())
                .append("discount", app.getPrice().getDiscount())
                .append("last_update", Vars.getDateFormat().format(app.getPrice().getLastUpdate()));
    }

    private static Document userDoc(User user) {
        return new Document("id", user.getUserId())
                .append("username", user.getUsername())
                .append("first_name", user.getFirstName())
                .append("last_name", user.getLastName());
    }

    /**
     *
     * @param user_id
     * @return список Steam ID по user_id
     */
    public static List<Integer> getUserApps(long user_id) {
        if (docExists(MongoDB.getAppsCollection(), user_id)) {
            return Utils.stringToList(getValue(MongoDB.getAppsCollection(), user_id).first().get("apps"));
        }
        return null;
    }

    /**
     *
     * @param collection
     * @param id
     * @return значение из collection по id и ключу key
     */
    public static FindIterable<Document> getValue(MongoCollection<Document> collection, long id) {
        return collection.find(new BasicDBObject("id", id));
    }

    /**
     *
     * @param collection
     * @param id
     * @return существует ли такой id в collection
     */
    public static boolean docExists(MongoCollection<Document> collection, long id) {
        long found = collection.countDocuments(Document.parse("{id : " + id + "}"));
        return found != 0;
    }
}
