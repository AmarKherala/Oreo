package net.amar.oreojava;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;

public class Util {

    private static final Dotenv dotenv = Dotenv
            .configure()
            .directory("src/main/resources")
            .load();

    public static String botToken() {
        return dotenv.get("TOKEN");
    }
    public static String serverId() {
        return dotenv.get("GID");
    }
    public static String ownerId() {
        return dotenv.get("OWNER_ID");
    }
}
