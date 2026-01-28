package net.amar.oreojava.commands.slash.owner;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SetBotActivity extends SlashCommand {

    public SetBotActivity() {
        this.name = "set-bot-activity";
        this.contexts = new InteractionContextType[]{
                InteractionContextType.BOT_DM,
                InteractionContextType.GUILD,
                InteractionContextType.PRIVATE_CHANNEL
        };
        this.category = Categories.staff;

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "activity-type","what activity type",true));
        options.add(new OptionData(OptionType.STRING, "activity-text","what text to display",true));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {

        String aActivityType = event.getOption("activity-type").getAsString();
        String aActivityText = event.getOption("activity-text").getAsString();

        switch (aActivityType) {
            case "w","watch","watching" -> {
                Oreo.getJDA().getPresence().setActivity(Activity.watching(aActivityText));
                event.replyFormat("**Successfully** updated activity to *Watching %s*",aActivityText).queue();
            }
            case "p","play","playing" -> {
                Oreo.getJDA().getPresence().setActivity(Activity.playing(aActivityText));
                event.replyFormat("**Successfully** updated activity to *Playing %s*",aActivityText).queue();
            }
            case "l","listen","listening" -> {
                Oreo.getJDA().getPresence().setActivity(Activity.listening(aActivityText));
                event.replyFormat("**Successfully** updated activity to *Listening to %s*",aActivityText).queue();

            }
            case "custom" -> {
                Oreo.getJDA().getPresence().setActivity(Activity.customStatus(aActivityText));
                event.replyFormat("**Successfully** updated activity to *%s*",aActivityText).queue();
            }
            default -> event.reply("""
                    Invalid activity type, use:
                    [w , watch, watching] For watching.
                    [p, play, playing] For playing.
                    [l, listen, listening] For listening.
                    [custom] For custom status.
                    """).setEphemeral(true).queue();
        }
    }
}