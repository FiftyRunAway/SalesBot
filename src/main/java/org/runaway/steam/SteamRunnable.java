package org.runaway.steam;

import org.runaway.constructors.App;
import org.runaway.database.UtilsDB;

import java.util.Date;

public class SteamRunnable implements Runnable {

    private int steamId;
    private App newApp;

    public SteamRunnable(int steamId) {
        this.steamId = steamId;
        this.newApp = null;
    }

    @Override
    public void run() {
        App app = UtilsDB.toApp(this.steamId);
        Date update = app.getPrice().getLastUpdate();
        long milliseconds = new Date().getTime() - update.getTime();
        if (milliseconds > 30000) {
            this.newApp = UtilsDB.updatePrice(app);
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public App getNewApp() {
        return newApp;
    }
}
