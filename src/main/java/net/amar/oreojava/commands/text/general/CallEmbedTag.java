package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.EmbedTag;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.SQLException;

public class CallEmbedTag extends Command {

    public CallEmbedTag() {
        this.name = "embedtag";
        this.help = "call an embed tag";
        this.arguments = "[id]";
        this.aliases = new String[]{"et"};
        this.category = Categories.general;
    }
    @Override
    protected void execute(CommandEvent event) {
        String EmbedTagId = event.getArgs();

        try {
            EmbedTag embedTag = DBGetter.getEmbedTag(EmbedTagId, Oreo.getConnection());
            if (embedTag==null) {
                event.reply("No tag was found");
                return;
            }

            EmbedBuilder em = new EmbedBuilder();
            em.setTitle(embedTag.getTitle(), event.getJDA().getSelfUser().getAvatarUrl());
            em.setDescription(embedTag.getDescription());
            em.setColor(Color.cyan);

            if (event.getMessage().getReferencedMessage()!=null) {
                event.getMessage().getReferencedMessage().replyEmbeds(em.build()).queue();
            } else event.reply(em.build());
        } catch (SQLException e) {
            event.replyFormatted("Failed to call **%s** with reason [%s]",EmbedTagId ,e.getMessage());
        }
    }
}
