package net.amar.oreojava.handlers;


import net.amar.oreojava.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

public class UrlRequest {
    private static final String API_URL = "https://bugs.mojang.com/api/jql-search-post";
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final OkHttpClient client = new OkHttpClient();

    public static String fetchMojoLog(String url) {
        Request req = new Request.Builder().url(url).build();
        try (Response res = client.newCall(req).execute()) {
            String bodyString = res.body().string();
            if (bodyString.isEmpty()) {
                Log.error("Log body returned null, somehow...");
                return null;
            }
            return bodyString;
        } catch (Exception e) {
            Log.error("Failed to fetch log from url ["+url+"]", e);
            return null;
        }
    }

    public static void fetchBug(Message m, String key ) {

        JSONObject payload = new JSONObject()
                .put("advanced", true)
                .put("search", "key = MC-" + key)
                .put("project", "MC");

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                m.reply(e.getMessage()).queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    m.reply("Response was unsuccessful").queue();
                    return;
                }
                String sbody = response.body().string();
                JSONObject body = new JSONObject(sbody);
                JSONArray issues = body.getJSONArray("issues");
                if (issues==null || issues.isEmpty()) {
                    m.reply("Couldn't find issue").queue();
                    return;
                }
                buildIssueEmbed(m, issues.getJSONObject(0), key);
            }
        });
    }

    private static void buildIssueEmbed(Message m, JSONObject issue, String number) {
        String key = issue.optString("key", "MC-" + number);

        JSONObject fields = issue.optJSONObject("fields");
        if (fields == null) {
            m.reply("No fields found for MC-" + number).queue();
            return;
        }

        String summary = fields.optString("summary", "No summary available");

        String status = "Unknown";

        JSONObject statusObj = fields.optJSONObject("status");
        if (statusObj != null) {
            status = statusObj.optString("name", "Unknown");
        } else {
            JSONObject resolution = fields.optJSONObject("resolution");
            if (resolution != null) {
                status = resolution.optString("name", "Unknown");
            }
        }

        String issueSince = "Unknown";
        JSONArray versions = fields.optJSONArray("versions");
        if (versions != null && !versions.isEmpty()) {
            issueSince = versions.getJSONObject(0).optString("name", "Unknown");
        }

        String link = "https://bugs.mojang.com/browse/" + key;
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Mojira Issue Tracker")
                .setColor(Color.CYAN)
                .addField("Issue", "**" + key + "**", false)
                .addField(
                        summary,
                        "[View on Mojira](" + link + ")",
                        false
                )
                .addField("Issue since", issueSince, true)
                .addField("Status", status, true);

        m.replyEmbeds(embed.build()).queue();
    }

}
