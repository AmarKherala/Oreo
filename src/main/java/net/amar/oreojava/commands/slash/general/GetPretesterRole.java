package net.amar.oreojava.commands.slash.general;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.amar.oreojava.Oreo;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class GetPretesterRole extends SlashCommand{

  public GetPretesterRole() {
    this.name = "get-pretester";
    this.help = "grants you pretester role";
    this.contexts = new InteractionContextType[] {
      InteractionContextType.GUILD
    };
  }

  @Override
  protected void execute(SlashCommandEvent event) {
            event.reply("""
                By clicking the button below you agree to follow these rules:
                1. You agree to not go offtopic in the channel.
                2. You agree to only text there when the dev's ping the role.
                Any violation to those points will immediately result in a 7d timeout.
                """).addComponents(
                  ActionRow.of(Button.success("ver", Emoji.fromUnicode("✅")))
        ).setEphemeral(true)
         .queue((s) -> {
           Oreo.getWaiter()
             .waitForEvent(ButtonInteractionEvent.class,
                 condition -> event.getUser().getIdLong()==condition.getUser().getIdLong(),
                 action -> {
                   if (action.getComponentId().equals("ver")) {
                     Member m = action.getMember();
                     Role r = Oreo.getPretesterRole();
                     event.getGuild().addRoleToMember(m, r).queue(
                         ss -> 
                         event.getHook()
                         .sendMessage("success!")
                         .setEphemeral(true)
                         .queue(),
                         ff ->
                         event.getHook()
                         .sendMessage("failure!! [%s]".formatted(ff))
                         ); 
                   }
                 });
         });
  }
}
