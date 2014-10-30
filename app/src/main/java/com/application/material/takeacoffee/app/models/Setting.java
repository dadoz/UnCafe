package com.application.material.takeacoffee.app.models;

/**
 * Created by davide on 29/05/14.
 */
public class Setting {
    private int position;
    private String id;
    private String name;
    private int iconResourceId;


    public Setting(String id, int position, int iconResourceId, String name) {
        this.id = id;
        this.iconResourceId = iconResourceId;
        this.name = name;
        this.position = position;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }
}
