package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class BanText extends Command {

    public BanText() {
        this.name = "ban";
        this.help = "ban someone from a guild";
        this.category = Categories.staff;
        this.aliases = new String[]{"banish","kill","cook"};
        this.arguments = "<@user> [reason]";
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+",2);
        Member member;
        String reason;
        StringBuilder sb = new StringBuilder();

        List<Message.Attachment> image = event.getMessage().getAttachments();

        if (args.length <= 1) {
            event.replyError("Please provide all the arguments");
            return;
        }

        for (Message.Attachment attachment : image) {
            sb.append(attachment.getUrl()).append("\n");
        }

        String uid = args[0].replaceAll("\\D", "");
        member = event.getGuild().retrieveMemberById(uid).complete();
        reason = args[1];
        event.replySuccess("Banned **"+member.getUser().getName()+"** For Reason: *"+reason+"*");
    }
}
