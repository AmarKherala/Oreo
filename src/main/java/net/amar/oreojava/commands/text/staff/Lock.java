package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class Lock extends Command {

    public Lock() {
        this.name = "lock";
        this.help = "lock threads anywhere with a label";
        this.arguments = "[label]";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        if (event.getChannel().getType().isThread()) {

            ThreadChannel thread = (ThreadChannel) event.getChannel();
            String opId = thread.getOwnerId();
            String uId = event.getAuthor().getId();

            if (event.getMember().hasPermission(Permission.BAN_MEMBERS) && args.isEmpty()) {
                thread.getManager().setLocked(true).queue(
                        s -> event.replySuccess("Channel locked with no label"),
                        f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
                );
                return;
            }

            if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                thread.getManager().setLocked(true).setName("[%s] %s".formatted(args, thread.getName())).queue(
                        s -> event.replySuccess("Channel locked with no label"),
                        f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
                );
                return;
            }
            if (opId.equals(uId) && args.isEmpty()) {
                thread.getManager().setLocked(true).queue(
                        s -> event.replySuccess("Channel locked with no label"),
                        f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
                );
                return;
            }
            if (opId.equals(uId)) {
                thread.getManager().setLocked(true).setName("[%s] %s".formatted(args, thread.getName())).queue(
                        s -> event.replySuccess("Channel locked with no label"),
                        f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
                );
            }
        }
    }
}
