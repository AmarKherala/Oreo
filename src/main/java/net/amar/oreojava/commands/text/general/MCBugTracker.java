package net.amar.oreojava.commands.text.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.amar.oreojava.commands.Categories;
import net.amar.oreojava.handlers.UrlRequest;

public class MCBugTracker extends Command {

    public MCBugTracker() {
        this.name = "MC";
        this.help = "view tracked Minecraft bugs on mijora";
        this.arguments = "[key]";
        this.aliases = new String[]{"mc"};
        this.category = Categories.general;
    }
    @Override
    protected void execute(CommandEvent event) {
        String key = event.getArgs();
        UrlRequest.fetchBug(event.getMessage(), key);
    }
}
