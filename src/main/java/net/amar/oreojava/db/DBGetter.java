package net.amar.oreojava.db;

import net.amar.oreojava.Log;
import net.amar.oreojava.db.tables.Case;
import net.amar.oreojava.db.tables.EmbedTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBGetter {

    private static final String getCaseByIdStmt = """
            SELECT * FROM trying
            WHERE caseId = ?
            """;
    private static final String getCaseByUserIdStmt = """
            SELECT * FROM trying
            WHERE userId = ?
            ORDER BY caseId ASC
            """;
    private static final String getEmbedTagStmt = """
            SELECT * FROM embedtags WHERE id = ?
            """;

    public static Case getCaseById(int caseId, Connection connection) {

        try (PreparedStatement ps = connection.prepareStatement(getCaseByIdStmt)) {
            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();

            String caseType;
            String userName;
            String userId;
            String modName;
            String modId;
            String reason;
            String duration = "NOT SET";
            int appealable;
            boolean appeal = true;

           if (!rs.next()) {
               Log.error("Case not found", new Throwable("No case was found with ID "+caseId));
               return null;
           }
                userId = rs.getString("userId");
                userName = rs.getString("userName");
                modId = rs.getString("modId");
                modName = rs.getString("modName");
                caseType = rs.getString("type");
                reason = rs.getString("reason");

                if (caseType.equals("BAN")) {
                    appealable = rs.getInt("appealable");
                    if (appealable==0) appeal = false;
                }
                if (caseType.equals("MUTE")) duration = rs.getString("duration");


            return new Case(
                    userId,
                    userName,
                    modId,
                    modName,
                    caseType,
                    reason,
                    duration,
                    appeal
            );
        } catch (SQLException e) {
            Log.error("Failed to get case from DB",e);
            return null;
        }
    }

    public static Map<Integer ,Case> getUserCases(String userId, Connection connection) {
        Map<Integer, Case> cases = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(getCaseByUserIdStmt)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()) {
                Log.error("Cases not found", new Throwable("No cases was found for User ID "+userId));
                return null;
            }

            do {
                boolean appealable = rs.getInt("appealable") != 0;
                cases.put(rs.getInt(1),
                        new Case(
                                rs.getString("userId"),
                                rs.getString("userName"),
                                rs.getString("modId"),
                                rs.getString("modName"),
                                rs.getString("type"),
                                rs.getString("reason"),
                                rs.getString("duration"),
                                appealable
                        )
                );
            } while (rs.next());

        } catch (SQLException e) {
            Log.error("Failure while trying to get cases for a user",e);
           return null;
        }
        return cases;
    }

    public static EmbedTag getEmbedTag(String tagId, Connection connection) throws SQLException{
        try (PreparedStatement ps = connection.prepareStatement(getEmbedTagStmt)) {
            ps.setString(1, tagId);
            String title;
            String description;

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                Log.error("Tag not found", new Throwable("No tag was found with ID "+tagId));
                return null;
            }
                title = rs.getString("title");
                description = rs.getString("description");

            return new EmbedTag(tagId, title, description);
        } catch (SQLException e) {
            Log.error("Failure while trying to load tag ["+tagId+"]", e);
            return null;
        }
    }

    public static List<EmbedTag> getEmbedTags(Connection connection) {
        String selectStmt = "SELECT * FROM embedtags";
        List<EmbedTag> embedTags = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(selectStmt)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                embedTags.add(
                        new EmbedTag(
                                rs.getString("id"),
                                rs.getString("title"),
                                rs.getString("description"))
                );
            }
            return embedTags;
        } catch (SQLException e) {
            Log.error("Failed to get EmbedTags",e);
            return null;
        }
    }
}
