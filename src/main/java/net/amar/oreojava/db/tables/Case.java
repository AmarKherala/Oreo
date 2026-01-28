package net.amar.oreojava.db.tables;

import net.amar.oreojava.db.annotations.*;

@Table(value="trying")
public class Case {

    @IntField
    @Primary(autoIncrement = true)
    int caseId;

    @StringField
    @NonNull
    String userId;

    @StringField
    @NonNull
    String userName;

    @StringField
    @NonNull
    String modId;

    @StringField
    @NonNull
    String modName;

    @StringField
    @NonNull
    String type;

    @StringField
    @NonNull
    String reason;

    @StringField
    String duration;

    @BooleanField
    boolean appealable;

    public Case (
            String userId,
            String userName,
            String modId,
            String modName,
            String type,
            String reason,
            String duration,
            boolean appealable
    ) {
        this.userName = userName;
        this.userId = userId;
        this.modName = modName;
        this.modId = modId;
        this.type = type;
        this.reason = reason;
        this.duration = duration;
        this.appealable = appealable;
    }

    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getModName() {
        return modName;
    }
    public String getModId() {
        return modId;
    }
    public String getType() {
        return type;
    }
    public String getReason() {
        return reason;
    }
    public String getDuration() {
        return duration;
    }
    public boolean isAppealable() {
        return appealable;
    }
}
