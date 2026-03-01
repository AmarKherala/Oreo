package net.amar.oreojava;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import net.amar.oreojava.commands.slash.general.GetEmoji;
import net.amar.oreojava.commands.slash.owner.SetBotActivity;
import net.amar.oreojava.commands.slash.staff.*;
import net.amar.oreojava.commands.text.general.CallEmbedTag;
import net.amar.oreojava.commands.text.general.GetPrefixes;
import net.amar.oreojava.commands.text.general.HostInfo;
import net.amar.oreojava.commands.text.general.MCBugTracker;
import net.amar.oreojava.commands.text.owner.AddData;
import net.amar.oreojava.commands.text.owner.EraseData;
import net.amar.oreojava.commands.text.owner.LeaveServers;
import net.amar.oreojava.commands.text.owner.RevokeInvites;
import net.amar.oreojava.commands.text.staff.*;
import net.amar.oreojava.db.DBGetter;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.db.DBTableBuilder;
import net.amar.oreojava.db.tables.Data;
import net.amar.oreojava.db.tables.EmbedTag;
import net.amar.oreojava.events.Honeypot;
import net.amar.oreojava.events.SupportThreads;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;

public class Oreo {

    private static JDA jda;
    private static CommandClientBuilder CmdClientBuilder;
    private static Connection connection;
    private static EventWaiter waiter;
    public static String[] prefixes = {"?", "m!", "$", ">"};
    public Oreo() throws InterruptedException, SQLException {

        waiter = new EventWaiter();
        connection = DriverManager.getConnection("jdbc:sqlite:amaroreo.db");
        DBTableBuilder.execute(connection, Case.class);
        DBTableBuilder.execute(connection, EmbedTag.class);
        DBTableBuilder.execute(connection, Data.class);

        CmdClientBuilder = new CommandClientBuilder();
        CmdClientBuilder.setOwnerId(Util.ownerId());
        CmdClientBuilder.setEmojis("✅", "⚠️", "❌");
        CmdClientBuilder.forceGuildOnly(Util.serverId());
        CmdClientBuilder.setPrefix("!!");
        CmdClientBuilder.setPrefixes(prefixes);
        CmdClientBuilder.addCommands(
                // No category
                new PingCommand(),

                // general
                new CallEmbedTag(),
                new GetPrefixes(),
                new MCBugTracker(),

                // staff
                new BanText(),
                new UnbanText(),
                new MuteText(),
                new KickText(),
                new SupportbanText(),
                new Warn(),
                new ModsBlacklist(),
                new Lock(),

                // owner
                new HostInfo(),
                new AddData(),
                new EraseData(),
                new RevokeInvites(),
                new LeaveServers()
        );
        CmdClientBuilder.addSlashCommands(
                new SetBotActivity(),
                new BanSlash(),
                new GetCase(),
                new GetCases(),
                new AddEmbedTag(),
                new GetEmbedTags(),
                new RemoveEmbedTag(),
                new GetEmoji(),
                new EditCase(),
                new MuteSlash(),
                new SupportbanSlash()
        );

        jda = JDABuilder.createLight(Util.botToken())
                .addEventListeners(
                        waiter,
                        CmdClientBuilder.build(),
                        new Honeypot(),
                        new SupportThreads()
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

    public static CommandClientBuilder getCmdClientBuilder() {
        return CmdClientBuilder;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static TextChannel getVerdictChannel() {
        String id = DBGetter.getData(connection, "verdict");
        if (id == null) return null;
        return jda.getTextChannelById(id);
    }

    public static ForumChannel getSupportChannel() {
        String id = DBGetter.getData(connection, "support");
        if (id == null) return null;
        return jda.getForumChannelById(id);
    }

    public static TextChannel getForbiddenChannel() {
        String id = DBGetter.getData(connection, "honeypot");
        if (id == null) return null;
        return jda.getTextChannelById(id);
    }

    public static Role getSupportbanRole() {
        String id = DBGetter.getData(connection, "support_ban");
        if (id == null) return null;
        return jda.getRoleById(id);
    }
}