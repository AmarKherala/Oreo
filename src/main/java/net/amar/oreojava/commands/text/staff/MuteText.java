package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.ParseMute;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class MuteText extends Command {

    public MuteText() {
        this.name = "mute";
        this.help = "mute a bad person";
        this.category = Categories.staff;
        this.aliases = new String[]{"m","shut","shush"};
        this.arguments = "<@user> [duration] [reason]";
        this.userPermissions = new Permission[]{
                Permission.MODERATE_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+", 3);

        if (args.length < 3) {
            event.replyError("Please provide all the arguments ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");
        String duration = args[1];
        String reason = args[2];

        String proof = event.getMessage().getAttachments().isEmpty()
                ? null
                : event.getMessage().getAttachments().get(0).getUrl();

        int amount;
        try {
            amount = Integer.parseInt(duration.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            event.replyError("Invalid duration format.");
            return;
        }

        Member m = event.getMember();
        event.getGuild().retrieveMemberById(uid).queue(member -> {
            if (!m.canInteract(member)) {
                event.replyError("You can't punish someone eho has a higher role");
                return;
            }
            boolean success = ParseMute.mute(
                    member.getUser(),
                    amount,
                    duration,
                    event.getGuild(),
                    reason
            );

            if (!success) {
                event.replyError("Something went wrong while muting.");
                return;
            }

            Case c = new Case(
                    member.getUser().getId(),
                    member.getUser().getName(),
                    event.getAuthor().getId(),
                    event.getAuthor().getName(),
                    "MUTE",
                    reason,
                    duration,
                    true
            );

            Verdict.buildVerdict(c, Oreo.getVerdictChannel(), member.getUser(), proof);

            event.replySuccess("Muted **%s** for **%s**".formatted(member.getUser().getName(), duration));
        }, failure -> event.replyError("User not found in this server."));
    }
}
