package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBInserter;
import net.amar.oreojava.db.tables.EmbedTag;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.sqlite.SQLiteErrorCode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddEmbedTag extends SlashCommand {

    public AddEmbedTag() {
        this.name = "add-embed-tag";
        this.help = "make a shortcut for a statement with a simple tag";
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "tag", "the id you call the statement with", true));
        options.add(new OptionData(OptionType.STRING, "title", "the title of the statement embed", true));
        options.add(new OptionData(OptionType.STRING, "description", "the statement itself", true));
        options.add(new OptionData(OptionType.BOOLEAN, "preview", "show a preview after execution", false));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String tagId = event.getOption("tag").getAsString();
        String title = event.getOption("title").getAsString();
        String description = event.getOption("description").getAsString();
        boolean preview = false;

        if (event.getOption("preview") != null) {
            preview = event.getOption("preview").getAsBoolean();
        }

        try {
            DBInserter.insert(Oreo.getConnection(), new EmbedTag(tagId, title, description));

            if (preview) event.replyEmbeds(new EmbedBuilder()
                            .setTitle(title)
                            .setDescription(description)
                            .build())
                    .setEphemeral(true)
                    .queue();

            else event.replyFormat("Added tag **%s**, Title: [%s]\nDescription: [%s]",tagId, title, description).setEphemeral(true).queue();
        } catch (SQLException e) {

            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY.code) {
                event.replyFormat("A tag with the ID **%s** already exists", tagId).queue();
                Log.error("Tried to insert an EmbedTab ID into table embedtags that already exists",e);
                return;
            }

            Log.error("Failure while trying to insert an EmbedTag object to DB", e);
            event.replyFormat("Failed to add tag with message [%s]", e.getMessage()).queue();
        }
    }
}
