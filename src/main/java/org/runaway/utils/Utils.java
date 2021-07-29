package org.runaway.utils;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static List<Integer> stringToList(Object str) {
        if (str == null) return null;
        String s = str.toString();
        List<Integer> l = new ArrayList<>();
        String[] spl = s.replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");
        if (!spl[0].equals("null")) {
            for (String s1 : spl) {
                l.add(Integer.parseInt(s1));
            }
        } else {
            return null;
        }
        return l;
    }

    public static synchronized Date stringToDate(SimpleDateFormat format, String string) {
        try {
            return format.parse(string);
        } catch (Exception e) {
            System.out.println("Problem str: " + string);
        }
        return new Date();
    }

    /**
     * Формирование имени пользователя
     * @param msg сообщение
     */
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     * @param user пользователь
     */
    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

    }

    public static void sendMessage(long user_id, String text) throws
            UnsupportedEncodingException, MalformedURLException {
        httpRequest(new URL("https://api.telegram.org/bot" + Vars.BOT_TOKEN.getString() + "/sendMessage?chat_id=" +
                user_id + "&text=" + URLEncoder.encode(text, "UTF-8") +
                "&parse_mode=HTML&disable_web_page_preview=true"));
    }

    public static String httpRequest(final URL url) {
        String result = "";
        try {
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result = String.valueOf(result) + line;
            }
            rd.close();
        }
        catch (Exception ex) {}
        return result;
    }
}
