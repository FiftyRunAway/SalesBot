package main.java.org.runaway.constructors;

import main.java.org.runaway.utils.AppType;

public class App {

    private int steamId;
    private String name;
    private Price price;
    private AppType appType;
    private String releaseDate;

    public App(int steamId, String name, AppType type, Price price, String releaseDate) {
        this.steamId = steamId;
        this.name = name;
        this.price = price;
        this.appType = type;
        this.releaseDate = releaseDate;
    }

    public int getSteamId() {
        return steamId;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public AppType getAppType() {
        return appType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
