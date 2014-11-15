package com.application.material.takeacoffee.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CoffeeMachine implements Parcelable{
    public static String COFFEE_MACHINE_ID_KEY = "COFFEE_MACHINE_ID";
    public static String COFFEE_MACHINE_OBJ_KEY = "COFFEE_MACHINE_OBJ";
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
