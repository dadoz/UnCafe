package com.application.material.takeacoffee.app.models;

import com.google.gson.annotations.SerializedName;

public class CoffeePlace {
    public static String COFFEE_PLACE_ID_KEY = "PLACE_ID_KEY";
    public static String COFFEE_PLACE_NAME_KEY = "PLACE_NAME_KEY";
    public static String COFFEE_MACHINE_OBJ_KEY = "COFFEE_MACHINE_OBJ";

    @SerializedName("place_id")
    private String id;
    private String iconPath;
	private String name;
    @SerializedName("vicinity")
	private String address;

    /**
     *
     * @param place_id
     * @param name
     * @param vicinity
     * @param iconPath
     */
    public CoffeePlace(String place_id, final String name, String vicinity, String iconPath) {
        this.id = place_id;
        this.iconPath = iconPath;
		this.name = name;
		this.address = vicinity;
	}

    /**
     *
     * @return
     */
	public String getName() {
		return name;
	}

    /**
     *
     * @return
     */
	public String getAddress() {
        return address;
	}

    /**
     *
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     */
    public String getIconPath() {
        return iconPath;
    }
}
