package net.amar.oreojava;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ServerinfoCommand;
import net.amar.oreojava.commands.slash.owner.SetBotActivity;
import net.amar.oreojava.commands.slash.staff.*;
import net.amar.oreojava.commands.text.general.CallEmbedTag;
import net.amar.oreojava.commands.text.staff.BanText;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.db.DBTableBuilder;
import net.amar.oreojava.db.tables.EmbedTag;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;

public class Oreo {

    private static JDA jda;
    private static Connection connection;
    public static final EventWaiter waiter = new EventWaiter();

    public Oreo() throws InterruptedException, SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:amaroreo.db");
        DBTableBuilder.execute(connection, Case.class);
        DBTableBuilder.execute(connection, EmbedTag.class);

        CommandClientBuilder CmdClientBuilder = new CommandClientBuilder();
        CmdClientBuilder.setOwnerId(Util.ownerId());
        CmdClientBuilder.setEmojis("✅", "⚠️", "❌");
        CmdClientBuilder.forceGuildOnly(Util.serverId());
        CmdClientBuilder.setPrefix("!!");
        CmdClientBuilder.addCommands(
                new PingCommand(),
                new ServerinfoCommand(),
                new CallEmbedTag(),
                new BanText()
        );
        CmdClientBuilder.addSlashCommands(
                new SetBotActivity(),
                new BanSlash(),
                new GetCase(),
                new GetCases(),
                new AddEmbedTag(),
                new GetEmbedTags(),
                new RemoveEmbedTag()
        );

        jda = JDABuilder.createLight(Util.botToken())
                .addEventListeners(
                        waiter,
                        CmdClientBuilder.build()
                )
                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .build()
                .awaitReady();
    }
    public static void main(String[] args) {
        try {
           new Oreo();
        } catch (Exception e) {
            Log.error("Failed to build bot instance",e);
        }
    }

    public static JDA getJDA() {
        return jda;
    }

    public static Connection getConnection() {
        return connection;
    }
}