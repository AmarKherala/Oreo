package net.amar.oreojava.handlers;

import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.db.DBInserter;
import net.amar.oreojava.db.DBUpdater;
import net.amar.oreojava.db.tables.Case;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/*
*  I might have made a mistake by naming this class because it does way more than what it's named as
*/
public class Verdict {

    public static boolean buildVerdict(Case mCase, TextChannel tc, User u, String proof) {

        if (tc==null) {
            Log.warn("verdict channel isn't set");
            return false;
        }

        try {
            EmbedBuilder em;
            long caseId = DBInserter.insert(Oreo.getConnection(), mCase);

            if (proof==null) em = caseEmbed(mCase, caseId, u, null);
            else em = caseEmbed(mCase, caseId, u, proof);

            boolean isAppealabel = (mCase.getType().equals("BAN") ||
                mCase.getType().equals("SUPPORT BAN")) && mCase.isAppealable();

            if (announceVerdictInDm(u, em, isAppealabel)) {
                tc.sendMessageEmbeds(em.build()).queue(
                        s -> {
                            try {
                                DBUpdater.updateMessageId(Oreo.getConnection(), caseId, s.getId());
                            } catch (SQLException e) {
                                Log.error("Couldn't set message id",e);
                            }
                        }
                );
            }
            return true;
        } catch (Exception e) {
            Log.error("Failed to announce verdict",e);
            return false;
        }
    }
    private static boolean announceVerdictInDm(User u, EmbedBuilder em, boolean isAppeal) {
       try  {

            u.openPrivateChannel().queue((s) -> {
              if (isAppeal)
                s.sendMessage("Your ban is appealable, you can appeal [here](https://forms.gle/2PB54RqZitH2FXaV7)").queue();
            });

            return true;
        } catch (Exception e) {
           Log.error("Failed to announce verdict in DMs",e);
           return false;
       }
    }

    private static EmbedBuilder caseEmbed(Case mCase, long caseId, User u, String proof) {
        EmbedBuilder em = new EmbedBuilder()
                .setTitle(mCase.getType() + " | #" + caseId)
                .addField("Moderator:", mCase.getModName()+" ("+mCase.getModId()+")",true)
                .addField("User:", mCase.getUserName()+" ("+mCase.getUserId()+")", true)
                .addField("Reason:",mCase.getReason(), true)
                .setColor(Color.cyan)
                .setThumbnail(u.getAvatarUrl())
                .setTimestamp(OffsetDateTime.now());
        if (proof!=null) em.setImage(proof);
        if (mCase.getType().equals("BAN")) em.addField("Appealable:",String.valueOf(mCase.isAppealable()), true);
        if (mCase.getType().equals("MUTE")) em.addField("Duration:",mCase.getDuration(),true);
        return em;
    }
}
