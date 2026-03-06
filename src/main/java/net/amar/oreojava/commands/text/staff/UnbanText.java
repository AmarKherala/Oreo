package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class UnbanText extends Command {

    public UnbanText() {
        this.name = "unban";
        this.help = "unban someone from a guild";
        this.category = Categories.staff;
        this.arguments = "<@user> [reason]";
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+",2);
        User u;
        String reason;
        String proof = null;
        if (!event.getMessage().getAttachments().isEmpty())
            proof = event.getMessage().getAttachments().get(0).getUrl();

        if (args.length <= 1) {
            event.replyError("Please provide all the arguments ``%s``".formatted(this.arguments));
            return;
        }

        String uid = args[0].replaceAll("\\D", "");
        u = event.getJDA().retrieveUserById(uid).complete();
        reason = args[1];

        Case c = new Case(
                u.getId(),
                u.getName(),
                event.getAuthor().getId(),
                event.getAuthor().getName(),
                "UNBAN",
                reason,
                "",
                true
        );
        final String uname = u.getName();
        Verdict.buildVerdict(c, Oreo.getVerdictChannel(), u, proof);
        event.getGuild().unban(u).reason(reason).queue(
                success ->  event.replySuccess("Unbanned **"+uname +"** For Reason: *"+reason+"*"),
                failure -> event.replyError("Couldn't unban %s \n[%s]".formatted(uname, failure.getMessage()))
                );
    }
}
