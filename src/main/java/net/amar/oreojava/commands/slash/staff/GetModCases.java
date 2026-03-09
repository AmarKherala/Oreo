package net.amar.oreojava.commands.slash.staff;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.amar.oreojava.commands.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class GetModCases extends SlashCommand {

    public GetModCases() {
        this.name = "get-mod-cases";
        this.help = "get moderation cases of a mod";
        this.options
                .add(new OptionData(OptionType.USER, "mod","the person to get its cases", true));
        this.category = Categories.staff;
        this.userPermissions = new Permission[]{
                Permission.BAN_MEMBERS
        };
        this.contexts = new InteractionContextType[]{
                InteractionContextType.GUILD
        };
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        User user = event.optUser("mod");
        GetUserCases.paginateCases(user, event, false);
    }
}
