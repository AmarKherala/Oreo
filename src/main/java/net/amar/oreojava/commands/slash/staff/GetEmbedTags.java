package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.EmbedTag;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetEmbedTags extends SlashCommand {

    public GetEmbedTags() {
        this.name = "get-embed-tags";
        this.help = "get all existing embed tags";
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        List<EmbedTag> embedTagList = DBGetter.getEmbedTags(Oreo.getConnection());
        List<MessageEmbed> embeds = new ArrayList<>();

        if (embedTagList==null) {
            event.reply("No tags were found").queue();
            return;
        }
        int page = 0;
        int totalPages = embedTagList.size();
        for (EmbedTag et : embedTagList) {
            page++;
            embeds.add(new EmbedBuilder()
                    .setTitle("# Embed Tags")
                    .setDescription("**ID:**\n```"+et.getId()+"```\n**Title:**\n```"+et.getTitle()+"```\n**Description:**\n```"+et.getDescription()+"```")
                    .setColor(Color.cyan)
                    .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                    .setFooter("Page "+page + " / " + totalPages)
                    .setTimestamp(OffsetDateTime.now())
                    .build());
        }
        EmbedPaginator embedPaginator = new EmbedPaginator.Builder()
                .setEventWaiter(Oreo.waiter)
                .setUsers(event.getUser())
                .waitOnSinglePage(false)
                .addItems(embeds)
                .setFinalAction(m -> m.clearReactions().queue())
                .setTimeout(1, TimeUnit.MINUTES)
                .build();
        embedPaginator.display(event.getHook());
        event.reply("Getting EmbedTags...").queue();
    }
}
