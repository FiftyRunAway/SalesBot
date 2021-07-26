package org.runaway.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.runaway.utils.Vars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class MongoDB {
    private static Logger logger = LoggerFactory.getLogger(MongoDB.class.getName());

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static boolean authenticate;
    private static MongoCollection<Document> users_collection;
    private static MongoCollection<Document> apps_collection;
    private static MongoCollection<Document> appids_collection;

    public MongoDB() {
        try {
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
            MongoClientURI connectionString = new MongoClientURI("mongodb+srv://" + Vars.DB_USERNAME.getString() + ":" + Vars.DB_PASSWORD.getString() + "@cluster.nosci.mongodb.net/" + Vars.DB_CLUSTER.getString() + "?retryWrites=true&w=majority");

            mongoClient = new MongoClient(connectionString);
            database = mongoClient.getDatabase(Vars.DB_DATABASE.getString());
            users_collection = database.getCollection(Vars.DB_COLLECTION_USERS.getString());
            apps_collection = database.getCollection(Vars.DB_COLLECTION_APPS.getString());
            appids_collection = database.getCollection(Vars.DB_COLLECTION_APPIDS.getString());
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("Connection to MongoDB is failed");
            return;
        }

        authenticate = true;
        logger.info("Бот подключился к MongoDB");
    }

    public static MongoCollection<Document> getAppsCollection() {
        return apps_collection;
    }

    public static MongoCollection<Document> getUsersCollection() {
        return users_collection;
    }

    public static MongoCollection<Document> getAppIdsCollection() {
        return appids_collection;
    }

    public static boolean isAuthenticate() {
        return authenticate;
    }
}
