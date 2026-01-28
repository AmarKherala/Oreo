package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.Case;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GetCases extends SlashCommand {

    public GetCases() {
        this.name = "get-cases";
        this.help = "find cases of a specific user";
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user","the person to get its cases", true));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User user = event.getOption("user").getAsUser();
        Map<Integer, Case> cases = DBGetter.getUserCases(user.getId(), Oreo.getConnection());
        if (cases == null) {
            event.replyFormat("User **%s** has no moderation cases",user.getName()).queue();
            return;
        }
        List<MessageEmbed> embeds = new ArrayList<>();

        int page = 0;
        int totalPages = cases.size();
        for (Map.Entry<Integer, Case> entry : cases.entrySet()){
            page++;
            int caseId = entry.getKey();
            Case cCase = entry.getValue();
            EmbedBuilder em = new EmbedBuilder();
            em.setTitle(cCase.getType()+" | #"+caseId);
            em.addField("**Moderator:**", cCase.getModName()+" ("+cCase.getModId()+")", true);
            em.addField("**User:**", cCase.getUserName()+" ("+cCase.getUserId()+")", true);
            em.addField("**Reason:**",cCase.getReason(), false);

            if (cCase.getType().equals("BAN"))
                em.addField("**Appealable:**", String.valueOf(cCase.isAppealable()), false);
            if (cCase.getType().equals("MUTE"))
                em.addField("**Duration:**",cCase.getDuration(), false);

            em.setThumbnail(user.getAvatarUrl());
            em.setTimestamp(OffsetDateTime.now());
            em.setFooter("Page "+page+" / "+totalPages);
            embeds.add(em.build());
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
        event.reply("Getting cases...").queue();
    }
}
