package net.amar.oreojava.commands.text.staff;

import net.amar.oreojava.commands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class Lock extends Command {

    public Lock() {
        this.name = "lock";
        this.help = "lock threads anywhere with a label";
        this.arguments = "[action (a, o)] [label]";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getChannel().getType().isThread()) {
            String args = event.getArgs();
            ThreadChannel thread = (ThreadChannel) event.getChannel();
            String opId = thread.getOwnerId();
            String uId = event.getAuthor().getId();

            boolean isStaff = event.getMember()
                    .hasPermission(Permission.MANAGE_THREADS);
            boolean isOp = opId.equals(uId);

            if (!(isOp || isStaff)) {
                event.replyError("You can't use that here");
                return;
            }

            if (thread.isLocked()) {
                event.replyError("Thread is already locked");
                return;
            }

            String[] arg;
            if (!args.isEmpty()) {
                arg = args.split("\\s+",2);
                String action = arg[0];
                String label = arg[1];

                if (label.isBlank()) {
                    event.replyError("Label can't be empty");
                    return;
                }

                switch (action) {
                    case "o","-o" -> lockOverride(thread, event, label);
                    case "a", "-a" -> lockAppend(thread, event, label);
                    default -> event.replyError("Unknown action, use ``a, -a`` to append label or ``o, -o`` to override");
                }
            } else event.replyError("Unknown pattern, use ```%s```".formatted(this.arguments));
        }
    }

    private void lockAppend(ThreadChannel thread, CommandEvent event, String label) {
        try {
            thread.getManager().setName("[%s] %s".formatted(label, thread.getName())).setLocked(true).queue(
                    s -> event.replySuccess("Channel locked with label [%s]".formatted(label)),
                    f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
            );
        } catch (IllegalArgumentException e) {
            event.replyError("Channel name too long, use ``o`` to override the name\n```!!lock o [label]```");
        } catch (Exception e) {
            event.replyError("Something went wrong...\n[%s]".formatted(e));
        }
    }

    private void lockOverride(ThreadChannel thread, CommandEvent event, String label) {
        try {
            thread.getManager().setName("[%s]".formatted(label)).setLocked(true).queue(
                    s -> event.replySuccess("Channel locked with label [%s]".formatted(label)),
                    f -> event.replyError("Failed to lock channel [%s]".formatted(f.getMessage()))
            );
        } catch (Exception e) {
            event.replyError("Something went wrong...\n[%s]".formatted(e));
        }
    }
}
