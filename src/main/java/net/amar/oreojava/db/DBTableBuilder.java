package net.amar.oreojava.db;

import net.amar.oreojava.Log;
import net.amar.oreojava.db.annotations.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTableBuilder {
    public static void execute(Connection conn, Class<?> clazz) {
        try (Statement stmt = conn.createStatement()) {
            String sql = build(clazz);
            Log.info("SQLite statement:\n"+sql);
            stmt.execute(sql);
            Log.info("DB ready!");
        } catch (SQLException e) {
            Log.error("Failure while trying to create DB",e);
        }
    }
    public static String build(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        StringBuilder sb = new StringBuilder(
                "CREATE TABLE IF NOT EXISTS "+table.value()+" ("
        );

        for (Field field : clazz.getDeclaredFields()) {
            validateField(field);
            sb.append(field.getName()).append(" ");
            if (field.isAnnotationPresent(IntField.class))
                sb.append("INTEGER");
            if (field.isAnnotationPresent(StringField.class))
                sb.append("TEXT");
            if (field.isAnnotationPresent(BooleanField.class))
                sb.append("INTEGER").append(" CHECK (")
                        .append(field.getName())
                        .append(" IN (0, 1))");
            if (field.isAnnotationPresent(NonNull.class))
                sb.append(" NOT NULL");
            if (field.isAnnotationPresent(Primary.class)) {
                sb.append(" PRIMARY KEY");
                if (field.getAnnotation(Primary.class).autoIncrement()) {
                    sb.append(" AUTOINCREMENT");
                }
            }
            sb.append(",");
        }

        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        return sb.toString();
    }

    private static void validateField(Field field) {
        boolean intField = field.isAnnotationPresent(IntField.class);
        boolean stringField = field.isAnnotationPresent(StringField.class);
        boolean booleanField = field.isAnnotationPresent(BooleanField.class);

        int count = 0;
        if (intField) count++;
        if (stringField) count++;
        if (booleanField) count++;

        if (count != 1) {
            Log.error(field.getName()+" field has more than one field annotation");
            throw new IllegalStateException("Field '" + field.getName() + "' must have exactly ONE type annotation");
        }
    }

}
