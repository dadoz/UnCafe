package com.application.material.takeacoffee.app.models;

public class CoffeeMachine {
    public static String COFFEE_MACHINE_ID_KEY = "COFFEE_MACHINE_ID";
    private final String objectId;
    private String iconPath;
	private String name;
	private String address;

	public CoffeeMachine(String objectId, final String name, String address , String iconPath) {
        this.objectId = objectId;
        this.iconPath = iconPath;
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

    public String getIconPath() {
        return iconPath;
    }

	public String getAddress(){
		return address;
	}

    public String getId(){
        return this.objectId;
    }

}
