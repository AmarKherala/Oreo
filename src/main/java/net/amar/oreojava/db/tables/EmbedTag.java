package net.amar.oreojava.db.tables;

import net.amar.oreojava.db.annotations.*;

@Table(value = "embedtags")
public class EmbedTag {

    @StringField
    @NonNull
    String id;

    @StringField
    @NonNull
    String title;

    @StringField
    @NonNull
    String description;

    public EmbedTag(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
