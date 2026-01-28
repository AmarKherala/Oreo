package net.amar.oreojava.db;

import net.amar.oreojava.Log;
import net.amar.oreojava.db.annotations.BooleanField;
import net.amar.oreojava.db.annotations.Primary;
import net.amar.oreojava.db.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBInserter {

    public static void insert(Connection connection, Object obj) throws SQLException {
        Class<?> clazz = obj.getClass();
        Table table = clazz.getAnnotation(Table.class);
        List<Field> fields = new ArrayList<>();

        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Primary.class) && f.getAnnotation(Primary.class).autoIncrement())
                continue;
            fields.add(f);
        }

        String insertStmt = insertStmtBuilder(fields, table.value());
        try (PreparedStatement ps = connection.prepareStatement(insertStmt)) {

            for (int i = 0; i < fields.toArray().length; i++) {
                Field f = fields.get(i);
                f.setAccessible(true);

                Object value = f.get(obj);
                if (f.isAnnotationPresent(BooleanField.class))
                    ps.setInt(i + 1, (boolean) value ? 1 : 0);
                else ps.setObject(i + 1, value);
            }
            ps.executeUpdate();
            Log.info("Successfully inserted into "+table.value()+" with SQLite stmt: "+insertStmt);
        } catch (SQLException | IllegalAccessException e) {
            Log.error("Unable to write to "+table.value()+" table from DB",e);
        }
    }

    private static String insertStmtBuilder(List<Field> fields, String table) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field f : fields) {
            columns.append(f.getName()).append(",");
            values.append("?,");
        }

        columns.deleteCharAt(columns.length() - 1);
        values.deleteCharAt(values.length() - 1);

        return "INSERT INTO "+table+" ("+columns+") VALUES ("+values+");";
    }
}
