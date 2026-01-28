package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.db.DBEraser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class RemoveEmbedTag extends SlashCommand {

    public RemoveEmbedTag() {
        this.name = "remove-embed-tag";
        this.help = "remove an embed tag";
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "tag", "tag to remove", true));
        this.options = options;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String tagId = event.getOption("tag").getAsString();
        if (DBEraser.eraseEmbedTag(tagId, Oreo.getConnection())) event.replyFormat("Successfully deleted Embed Tag [%s]",tagId).queue();
        else event.replyFormat("Couldn't find or delete Embed Tag [%s]",tagId).queue();
    }
}
