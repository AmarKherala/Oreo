package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class Warn extends Command {

    public Warn() {
        this.name = "warn";
        this.help = "warn a bad person";
        this.category = Categories.staff;
        this.arguments = "<@user> [reason]";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.MODERATE_MEMBERS
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+", 2);

        if (args.length < 2) {
            event.replyError("Please provide all the arguments ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");
        String reason = args[1];

        String proof = event.getMessage().getAttachments().isEmpty()
                ? null
                : event.getMessage().getAttachments().get(0).getUrl();

        Member m = event.getMember();
        event.getGuild().retrieveMemberById(uid).queue((mm) -> {
                    if (m.canInteract(mm)) {
                        Case c = new Case(
                                mm.getUser().getId(),
                                mm.getUser().getName(),
                                m.getUser().getId(),
                                m.getUser().getName(),
                                "WARN",
                                reason,
                                "",
                                true
                        );
                        Verdict.buildVerdict(c, Oreo.getVerdictChannel(), mm.getUser(), proof);
                        event.replySuccess("Warned **%s** for *%s*".formatted(mm.getEffectiveName(), reason));

                    } else {
                        event.replyError("You can't punish a member higher than you");
                    }
                },
                failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage()))
        );
    }
}
