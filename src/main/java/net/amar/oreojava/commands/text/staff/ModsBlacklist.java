package net.amar.oreojava.commands.text.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.Util;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.json.JSONArray;

public class ModsBlacklist extends Command {

    public ModsBlacklist() {
        this.name = "blacklist";
        this.help = "mods blacklist";
        this.arguments = "[action(add, remove)] [mod_name]";
        this.category = Categories.staff;
        this.contexts = new InteractionContextType[]
                {InteractionContextType.GUILD};
        this.userPermissions = new Permission[]
                {Permission.KICK_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        JSONArray mods = Util.getBlacklistedMods();
        StringBuilder sb = new StringBuilder();
        if (event.getArgs().isEmpty() && mods!=null) {
            for (int i = 0; i < mods.length(); i++)
                sb.append(mods.get(i)).append("\n");
            event.replySuccess("```\n%s\n```".formatted(sb.toString()));
            return;
        }
        String[] args = event.getArgs().split("\\s+",2);
        if (args.length < 2) {
            event.replyError("Please provide all arguments %s".formatted(this.arguments));
            return;
        }
        String action = args[0];
        String mod_name = args[1];

        switch (action) {
            case "add" -> {
                if (Util.addToModsBlacklist(mod_name))
                    event.replySuccess("Added **%s** to blacklist".formatted(mod_name));
                else event.replyError("Failed to add **%s** to blacklist".formatted(mod_name));
            }
            case "remove" -> {
                if (Util.removeFromModBlacklist(mod_name))
                    event.replySuccess("Removed **%s** from blacklist".formatted(mod_name));
                else event.replyError("Failed to remove **%s** from blacklist".formatted(mod_name));
            }
        }
    }
}
