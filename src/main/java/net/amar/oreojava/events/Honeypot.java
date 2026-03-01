package net.amar.oreojava.events;

import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.ParseMute;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Honeypot extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.isFromGuild()) return;
        if (Oreo.getForbiddenChannel() == null) {
            Log.warn("honeypot isn't set");
            return;
        }

        if (!event.getChannel().getId().equals(Oreo.getForbiddenChannel().getId()))
            return;
        User user = event.getAuthor();
        deleteRecentMessagesGuildWide(event.getGuild(), user);
        event.getMessage().delete().reason("honeypot").queue();

        Case c = new Case(
                user.getId(),
                user.getName(),
                event.getJDA().getSelfUser().getId(),
                event.getJDA().getSelfUser().getName(),
                "MUTE",
                "Fell in the honeypot",
                "1 day",
                true
        );

        if (Verdict.buildVerdict(c, Oreo.getVerdictChannel(), user, null))
            Log.info(user.getName() + " tried to send a message in forbidden channel");
        ParseMute.mute(user, 1, "d", event.getGuild(), "honeypot");
    }

    private void deleteRecentMessagesGuildWide(Guild guild, User user) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusMinutes(15);

        for (TextChannel channel : guild.getTextChannels()) {
            channel.getHistory().retrievePast(100).queue(messages -> {
                List<Message> toDelete = new ArrayList<>();

                for (Message msg : messages) {
                    if (msg.getTimeCreated().isBefore(cutoff))
                        break;

                    if (msg.getAuthor().getIdLong() == user.getIdLong())
                        toDelete.add(msg);
                }

                if (!toDelete.isEmpty())
                    channel.purgeMessages(toDelete);
            });
        }
    }
}
