package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.Case;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetCase extends SlashCommand {

    public GetCase() {
        this.name = "get-case";
        this.help = "find a specific case by its id";
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "case","the id of desired case", true));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        int caseId = event.getOption("case").getAsInt();
        EmbedBuilder em = new EmbedBuilder();
        Case cCase = DBGetter.getCaseById(caseId, Oreo.getConnection());

        if (cCase == null) {
            event.replyFormat("No case was found with ID #%d",caseId).queue();
            return;
        }

        event.getJDA().retrieveUserById(cCase.getUserId()).queue((s)->{
                em.setColor(Color.cyan);
                em.setTitle(cCase.getType()+" | #"+caseId);
                em.addField("**Moderator:**", cCase.getModName()+" ("+cCase.getModId()+")", true);
                em.addField("**User:**", cCase.getUserName()+" ("+cCase.getUserId()+")", true);
                em.addField("**Reason:**",cCase.getReason(), false);

                if (cCase.getType().equals("BAN"))
                    em.addField("**Appealable:**", String.valueOf(cCase.isAppealable()), false);
                if (cCase.getType().equals("MUTE"))
                    em.addField("**Duration:**",cCase.getDuration(), false);

                em.setThumbnail(s.getAvatarUrl());
                em.setTimestamp(OffsetDateTime.now());
                event.replyEmbeds(em.build()).queue();
            });
    }
}
