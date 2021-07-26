package org.runaway.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.runaway.constructors.App;
import org.runaway.constructors.Price;
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
     * @param user_id
     * @param username
     * @param firstName
     * @param lastName
     */
    public static void registerUser(long user_id, String username, String firstName, String lastName) {
        if (!docExists(MongoDB.getUsersCollection(), user_id)) {
            Document doc = new Document("id", user_id)
                    .append("username", username)
                    .append("first_name", firstName)
                    .append("last_name", lastName);
            MongoDB.getUsersCollection().insertOne(doc);
            logger.debug(String.format("Пользователь %s. Зарегистрирован в DB users!", username));
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
            MongoDB.getAppsCollection().replaceOne(new BasicDBObject("id", user_id),
                    new Document("id", user_id)
            .append("apps", apps.toString()));
        } else {
            Document doc = new Document("id", user_id).append("apps", apps.toString());
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

                MongoDB.getAppsCollection().replaceOne(new BasicDBObject("id", user_id),
                        new Document("id", user_id).append("apps", apps.isEmpty() ? null : apps.toString()));
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param steamId игры, которая будет занесена в MongoDB
     */
    public static boolean saveSteamID(int steamId) {
        if (!isAppInBase(steamId)) {
            App app = Steam.getApp(steamId);
            MongoDB.getAppIdsCollection().insertOne(appDoc(app));
            logger.debug(String.format("Сохранена новый Steam ID в DB: %s", steamId));
            return true;
        }
        return false;
    }

    /**
     *
     * @param steamId
     * @return App с данными из MongoDB
     */
    public static App toApp(int steamId) {
        Document d = getValue(MongoDB.getAppIdsCollection(), steamId).first();

        String name = d.get("name").toString();
        AppType type = AppType.valueOf(d.get("type").toString());
        String release = d.get("release_date").toString();
        String formated_price = d.get("formated_price") == null ? "" : d.get("formated_price").toString();
        double discount = Double.parseDouble(d.get("discount").toString());
        double initial_price = Double.parseDouble(d.get("initial_price").toString());
        double final_price = Double.parseDouble(d.get("final_price").toString());
        Date last_update = Utils.stringToDate(Vars.getDateFormat(), d.get("last_update").toString());

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

            App newApp = new App(app.getSteamId(), app.getName(), app.getAppType(),
                    new Price(app.getAppType() == AppType.GAME, newPrice.getFormated_price(),
                            newPrice.getDiscount(), newPrice.getInitial_price(), newPrice.getFinal_price(), new Date()), app.getReleaseDate());

            MongoDB.getAppIdsCollection().replaceOne(new BasicDBObject("id", app.getSteamId()),
                    appDoc(newApp));

            return newApp;
        }
        return null;
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
    private static boolean docExists(MongoCollection<Document> collection, long id) {
        long found = collection.countDocuments(Document.parse("{id : " + id + "}"));
        return found != 0;
    }
}
