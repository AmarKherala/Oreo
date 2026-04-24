package net.amar.oreojava.commands.slash.staff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.amar.oreojava.Oreo;
import net.amar.sqloreo.utils.MathQuestionFormatter;
import net.amar.sqloreo.utils.MathQuestionUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class IQTest extends SlashCommand {

  public IQTest() {
    this.name = "iq";
    this.help = "test the iq of a person";
    this.contexts = new InteractionContextType[] {
      InteractionContextType.GUILD
    };
    this.userPermissions = new Permission[]{
      Permission.MODERATE_MEMBERS
    };
    this.options.add(new OptionData(OptionType.USER, "user", "user to iq test"));
  }

	@Override
	protected void execute(SlashCommandEvent event) {

    User u = event.optUser("user");
    MathQuestionUtils.Question q = MathQuestionUtils.generateQ(3);
    int answer = MathQuestionUtils.solveQ(q);

    // make two numbers close to the answer 
    Random r = new Random();
    int a = answer - r.nextInt(10, 25);
    int b = answer - r.nextInt(5, 19);

    List<Button> btns = new ArrayList<>();
    btns.add(Button.primary("answer", String.valueOf(answer)));
    btns.add(Button.primary("r1", String.valueOf(a)));
    btns.add(Button.primary("r2", String.valueOf(b)));
    Collections.shuffle(btns);

    EmbedBuilder em = new EmbedBuilder()
      .setTitle("IQ Test!")
      .setDescription("whats "+ MathQuestionFormatter.expressionBuilder(q.getNumbers(), q.getOps()))
      .setColor(Color.CYAN);

    EmbedBuilder success = new EmbedBuilder()
      .setTitle("SUCCESS!!")
      .setDescription("YOU DID IT RIGHT!!!!")
      .setColor(Color.GREEN);

    EmbedBuilder fail = new EmbedBuilder()
      .setTitle("FAILED!!!!")
      .setDescription("WHAT A FAILURE HAHAAA!!!!!")
      .setColor(Color.RED);

    event.reply(u.getAsMention())
      .addEmbeds(em.build())
      .addComponents(ActionRow.of(btns))
      .queue(s -> {
        Oreo.getWaiter().waitForEvent(ButtonInteractionEvent.class,
            con -> con.getUser().getIdLong()==u.getIdLong(),
            acc -> {
              String an = acc.getComponentId();
              if (an.equals("answer")) 
                s.editOriginalEmbeds(success.build()).queue();
              else s.editOriginalEmbeds(fail.build()).queue();
            },
            1, TimeUnit.MINUTES,
            () -> s.editOriginal("Expired!").queue());
      });
	} 
}
