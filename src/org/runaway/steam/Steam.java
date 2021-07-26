package org.runaway.steam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.runaway.constructors.App;
import org.runaway.constructors.Price;
import org.runaway.utils.AppType;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.io.IOException;
import java.util.Date;

public class Steam {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static String getResponse(int steamId, String filters) {
        HttpGet request = new HttpGet("http://store.steampowered.com/api/appdetails?appids=" + steamId +
                "&cc=RU&l=russian&filters=" + filters);
        request.addHeader("custom-key", "mkyong");
        request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param steamId
     * @return App с данными из Steam API
     */
    public static App getApp(int steamId) {
        try {
            String response_name = getResponse(steamId, "basic");
            String[] spl = response_name.substring(10, 80).split(",");
            String name = spl[2].replace("\"name\":", "").replace("\"", "");

            String response_releaseDate = getResponse(steamId, "release_date");
            boolean released = Boolean.parseBoolean(response_releaseDate.split(":")[5].replace(",\"date\"", ""));
            String releaseDate = response_releaseDate.split(":")[6].replace("\"", "").replace("}}}}", "");

            String response_priceoverview = getResponse(steamId, "price_overview");
            spl = response_priceoverview.split(":");
            AppType type;
            if (released) {
                type = AppType.PREORDER;
            } else type = AppType.GAME;
            double initial_price = 0;
            double final_price = 0;
            double discount = 0;
            String formated = null;
            if (spl.length < 6) {
                type = AppType.NOINFO;
            } else {
                initial_price = Double.parseDouble(spl[6].replace(",\"final\"", "")) / 100;
                final_price = Double.parseDouble(spl[7].replace(",\"discount_percent\"", "")) / 100;
                discount = Integer.parseInt(spl[8].replace(",\"initial_formatted\"", "")) % 100;
                formated = spl[10].replace("\"", "").replace("}}}}", "");
            }
            Price price = new Price(!released, formated, discount, initial_price, final_price, new Date());
            return new App(steamId, name, type, price, releaseDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Price getOnlyPrice(int steamId, boolean released) {
        String response_priceoverview = getResponse(steamId, "price_overview");
        String[] spl = response_priceoverview.split(":");
        double initial_price = 0;
        double final_price = 0;
        double discount = 0;
        String formated = null;
        if (spl.length > 6) {
            initial_price = Double.parseDouble(spl[6].replace(",\"final\"", "")) / 100;
            final_price = Double.parseDouble(spl[7].replace(",\"discount_percent\"", "")) / 100;
            discount = Integer.parseInt(spl[8].replace(",\"initial_formatted\"", "")) % 100;
            formated = spl[10].replace("\"", "").replace("}}}}", "");
        }
        return new Price(!released, formated, discount, initial_price, final_price, new Date());
    }
}
