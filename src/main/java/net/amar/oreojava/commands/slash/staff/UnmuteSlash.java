package net.amar.oreojava.commands.slash.staff;

import java.util.ArrayList;
import java.util.List;

import net.amar.oreojava.Oreo;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.handlers.Verdict;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;


public class UnmuteSlash extends SlashCommand {

    public UnmuteSlash() {
      this.name = "unmute";
      this.help = "remove someones timeout";
      this.contexts = new InteractionContextType[] {
        InteractionContextType.GUILD
      };
      this.userPermissions = new Permission[] {
        Permission.MODERATE_MEMBERS
      };
      
      List<OptionData> options = new ArrayList<>();
      options.add(new OptionData(OptionType.USER, "user", "user to unmute", true));
      options.add(new OptionData(OptionType.STRING, "reason", "reason to unmute", true));

      this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
      User user = event.optUser("user");
      String reason = event.optString("reason");
      Guild g = event.getGuild();

      if (!(user==null || reason==null || g==null)) {
        g.removeTimeout(user).reason(reason).queue(s -> {
          Case c = new Case(
              user.getId(),
              user.getName(),
              event.getUser().getId(),
              event.getUser().getName(),
              "UNMUTE",
              reason,
              "",
              true
              );
          if (Verdict.buildVerdict(c, Oreo.getVerdictChannel(), user, null)) {
            event.reply("Unmuted **%s**".formatted(user.getName())).queue();
          }
        });
      }
    }
}
