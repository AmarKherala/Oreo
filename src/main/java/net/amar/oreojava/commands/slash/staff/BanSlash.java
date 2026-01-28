package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.db.DBInserter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class BanSlash extends SlashCommand {

    public BanSlash() {
        this.name = "ban";
        this.help = "ban a bad user";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[] {
                Permission.BAN_MEMBERS
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "user to ban", true));
        options.add(new OptionData(OptionType.STRING, "reason", "why ban this user", true));
        options.add(new OptionData(OptionType.BOOLEAN, "appealable", "can they appeal their ban?", false));
        options.add(new OptionData(OptionType.ATTACHMENT, "proof", "optional proof", false));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User moderator = event.getUser();
        User user = event.getOption("user").getAsUser();
        String reason = event.getOption("reason").getAsString();
        boolean appeal = true;

        if (event.getOption("appealable")!=null)
             appeal = event.getOption("appealable").getAsBoolean();

        Case modCase = new Case(
                user.getId(),
                user.getName(),
                moderator.getId(),
                moderator.getName(),
                "BAN",
                reason,
                " ",
                appeal
        );
        try {
            DBInserter.insert(Oreo.getConnection(), modCase);
            event.replyFormat("Banned **%s** for Reason: **%s**", user.getName(), reason).queue();
        } catch (Exception e) {
            Log.error("Something went wrong while executing /ban command",e);
            event.reply(e.getMessage()).queue();
        }
    }
}
