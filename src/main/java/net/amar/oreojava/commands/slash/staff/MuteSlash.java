package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.ParseMute;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class MuteSlash extends SlashCommand {

    public MuteSlash() {
        this.name = "mute";
        this.help = "mute a bad user";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[] {
                Permission.MODERATE_MEMBERS
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "user to ban", true));
        options.add(new OptionData(OptionType.STRING, "reason", "why ban this user", true));
        options.add(new OptionData(OptionType.STRING, "duration", "for how long should they stay muted", true));
        options.add(new OptionData(OptionType.ATTACHMENT, "proof", "optional proof", false));

        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        Member mod = event.getMember();
        Member u = event.getOption("user").getAsMember();
        String reason = event.getOption("reason").getAsString();
        String duration = event.getOption("duration").getAsString();
        Message.Attachment image = null;
        if (event.getOption("proof")!=null)
            image = event.getOption("proof").getAsAttachment();

        assert u != null;
        assert mod != null;
        int amount = Integer.parseInt(duration.replaceAll("\\D+", ""));
        if (ParseMute.mute(u.getUser(), amount, duration, event.getGuild(), reason)) {

            if (!mod.canInteract(u)) {
                event.reply("You can't punish a person with a higher tole").queue();
                return;
            }

            Case c = new Case(
                    u.getUser().getId(),
                    u.getUser().getName(),
                    mod.getUser().getId(),
                    mod.getUser().getName(),
                    "MUTE",
                    reason,
                    duration,
                    true
            );
            if (image == null) Verdict.buildVerdict(c, Oreo.getVerdictChannel(), u.getUser(), null);
            else Verdict.buildVerdict(c, Oreo.getVerdictChannel(), u.getUser(), image.getUrl());
            event.replyFormat("Muted **%s** for **%s**", u.getUser().getName(), duration).queue();
        }
    }
}
