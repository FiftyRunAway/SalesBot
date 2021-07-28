package org.runaway.steam;

import org.runaway.constructors.App;
import org.runaway.database.UtilsDB;

import java.util.Date;
import java.util.List;

public class SteamRunnable extends Thread {

    private int steamId;

    public SteamRunnable(int steamId) {
        this.steamId = steamId;
    }

    @Override
    public void run() {
        try {
            App app = UtilsDB.toApp(this.steamId);
            Date update = app.getPrice().getLastUpdate();
            long milliseconds = new Date().getTime() - update.getTime();
            if (milliseconds > 900000) {
                UtilsDB.updatePrice(app);
            }
            interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateAll(List<Integer> apps) {
        for (Integer id : apps) {
            Thread thread = new Thread(new SteamRunnable(id));
            thread.start();
        }
    }
}
