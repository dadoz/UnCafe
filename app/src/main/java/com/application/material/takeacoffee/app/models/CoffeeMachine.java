package com.application.material.takeacoffee.app.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CoffeeMachine implements Parcelable {
    public static String COFFEE_MACHINE_ID_KEY = "COFFEE_MACHINE_ID";
    public static String COFFEE_MACHINE_OBJ_KEY = "COFFEE_MACHINE_OBJ";
    public static String COFFEE_MACHINE_STRING_KEY = "COFFEE_MACHINE_STRING_KEY";

//    @SerializedName("objectId")
    private final String id;
    private String iconPath;
	private String name;
	private String address;
    private Bitmap photo;

    public CoffeeMachine(Parcel in) {
        this.id = in.readString();
        this.iconPath = in.readString();
        this.name = in.readString();
        this.address = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.iconPath);
        dest.writeString(this.name);
        dest.writeString(this.address);
    }

    public static Creator CREATOR = new Creator() {
        @Override
        public CoffeeMachine createFromParcel(Parcel source) {
            return new CoffeeMachine(source);
        }

        @Override
        public CoffeeMachine[] newArray(int size) {
            return new CoffeeMachine[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
