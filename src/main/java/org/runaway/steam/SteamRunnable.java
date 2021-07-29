package org.runaway.steam;

import org.runaway.constructors.App;
import org.runaway.database.UtilsDB;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SteamRunnable extends Thread {

    private int steamId;
    public static HashMap<Integer, App> oldApps = new HashMap<>();
    public static HashMap<Integer, App> newApps = new HashMap<>();

    public SteamRunnable(int steamId) {
        this.steamId = steamId;
    }

    public SteamRunnable() {}

    @Override
    public void run() {
        try {
            App app = UtilsDB.toApp(this.steamId);
            getOldApps().put(app.getSteamId(), app);
            Date update = app.getPrice().getLastUpdate();
            long milliseconds = new Date().getTime() - update.getTime();
            if (milliseconds > 900000) { //900000 минимум
                App newApp = UtilsDB.updatePrice(app);
                getNewApps().put(this.steamId, newApp);
            } else {
                getNewApps().put(this.steamId, app);
            }
            interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SteamRunnable updateAll(List<Integer> apps) {
        for (Integer id : apps) {
            Thread thread = new Thread(new SteamRunnable(id));
            thread.start();
        }
        return this;
    }

    public static HashMap<Integer, App> getNewApps() {
        return newApps;
    }

    public static HashMap<Integer, App> getOldApps() {
        return oldApps;
    }
}
