package net.amar.oreojava.db;

import net.amar.oreojava.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBEraser {

    private static final String eraseEmbedTagStmt = """
            DELETE FROM embedtags
            WHERE id = ?
            """;
    public static boolean eraseEmbedTag(String id, Connection connection) {
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
}
