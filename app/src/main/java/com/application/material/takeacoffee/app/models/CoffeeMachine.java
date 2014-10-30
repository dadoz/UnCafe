package com.application.material.takeacoffee.app.models;

public class CoffeeMachine {
    private final String id;
    private String iconPath;
	private String name;
	private String address;

	public CoffeeMachine(String id, final String name, String address , String iconPath) {
        this.id = id;
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
        return this.id;
    }

}
