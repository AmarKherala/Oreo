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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class BanText extends Command {

    public BanText() {
        this.name = "ban";
        this.help = "ban someone from a guild";
        this.category = Categories.staff;
        this.aliases = new String[]{"banish","kill","cook"};
        this.arguments = "<@user> [reason]";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
    }
    @Override
    protected void execute(@NotNull CommandEvent event) {
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
        Member mem = event.getGuild().retrieveMemberById(uid).complete();
        event.getJDA().retrieveUserById(uid).queue((mm) -> {
      
            if (m.canInteract(mem)) {
                event.getGuild().ban(mm, 0, TimeUnit.DAYS)
                        .reason(reason)
                        .queue(success -> {
                            Case c = new Case(
                                    mm.getId(),
                                    mm.getName(),
                                    m.getUser().getId(),
                                    m.getUser().getName(),
                                    "BAN",
                                    reason,
                                    "",
                                    true
                            );
                            Verdict.buildVerdict(c, Oreo.getVerdictChannel(), mm, proof);
                            event.replySuccess("Banned **%s** for *%s*".formatted(mm.getEffectiveName(), reason));
                        });
            } else {
                event.replyError("You can't punish a member higher than you");
            }
                },
                failure -> event.replyError("Something went wrong\n[%s]".formatted(failure.getMessage()))
                );
    }
}
