package net.amar.oreojava.db;

import net.amar.oreojava.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBEraser {

    public static boolean eraseEmbedTag(String id, Connection connection) {
       final String eraseEmbedTagStmt = """
            DELETE FROM embedtags
            WHERE id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(eraseEmbedTagStmt)) {
            ps.setString(1, id);
            int deletedRows = ps.executeUpdate();
            Log.info("Successfully deleted "+deletedRows+" rows from EmbedTags table");
            return true;
        } catch (SQLException e) {
            Log.error("Failed to delete from EmbedTags table",e);
            return false;
        }
    }

    public static boolean eraseData(String fieldName, Connection connection) {
        final String eraseDataStmt = """
                DELETE FROM data
                WHERE fieldName = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(eraseDataStmt)) {
            ps.setString(1, fieldName);
            int deletedRows = ps.executeUpdate();
            Log.info("Successfully deleted "+deletedRows+" rows from data table");
            return true;
        } catch (SQLException e) {
            Log.error("Failed to delete from data table",e);
            return false;
        }
    }
}
