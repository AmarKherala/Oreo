package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.amar.oreojava.Oreo;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class UnmuteText extends Command {

  public UnmuteText() {
    this.name = "unmute";
    this.help = "remove timeout from someone";
    this.arguments = "<@user> [reason]";
    this.contexts = new InteractionContextType[]{
      InteractionContextType.GUILD
    };
    this.userPermissions = new Permission[] {
      Permission.MODERATE_MEMBERS
    };
  }

  @Override
  protected void execute(CommandEvent event) {
    String args = event.getArgs();
    String[] arargs = args.split("\\s+",2);
    String uid = arargs[0].replaceAll("\\D", "");
    String reason = arargs[1];
    Guild g = event.getGuild();

    try {
      event.getJDA().retrieveUserById(uid).queue(user -> {
        if (!(user == null || reason == null || g == null)) {
          g.removeTimeout(user).reason(reason).queue(s -> {
            Case c = new Case(
                    user.getId(),
                    user.getName(),
                    event.getAuthor().getId(),
                    event.getAuthor().getName(),
                    "UNMUTE",
                    reason,
                    "",
                    true
            );
            if (Verdict.buildVerdict(c, Oreo.getVerdictChannel(), user, null)) {
              event.replySuccess("Unmuted **%s**".formatted(user.getName()));
            }
          });
        }
      });
    } catch (NumberFormatException e) {
      event.replyError("Invalid ID or User");
    } catch (Exception e) {
      event.replyError("Something went wrong\n[%s]".formatted(e));
    }
  }
}
