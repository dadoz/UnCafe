package com.application.material.takeacoffee.app.models;

public class CoffeePlace {
    public static String COFFEE_PLACE_ID_KEY = "PLACE_ID_KEY";
    public static String COFFEE_PLACE_NAME_KEY = "PLACE_NAME_KEY";
    public static String COFFEE_MACHINE_OBJ_KEY = "COFFEE_MACHINE_OBJ";
    private String id;
    private String iconPath;
	private String name;
	private String address;

    /**
     *
     * @param id
     * @param name
     * @param address
     * @param iconPath
     */
    public CoffeePlace(String id, final String name, String address, String iconPath) {
        this.id = id;
        this.iconPath = iconPath;
		this.name = name;
		this.address = address;
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
