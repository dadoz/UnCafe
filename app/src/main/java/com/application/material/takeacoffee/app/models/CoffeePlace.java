package com.application.material.takeacoffee.app.models;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
    private ArrayList<PlacePhoto> photos;

    /**
     *
     * @param place_id
     * @param name
     * @param vicinity
     * @param iconPath
     */
    public CoffeePlace(String place_id, final String name, String vicinity, String iconPath,
                       ArrayList<PlacePhoto> photos) {
        this.id = place_id;
        this.iconPath = iconPath;
		this.name = name;
		this.address = vicinity;
        this.photos = photos;
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

    /**
     *
     * @return
     */
    public String getPhotoReference() {
        return photos != null ? photos.get(0).getPhotoReference() : null;
    }

    public static class PlacePhoto {
        private int height;
        private String photo_reference;
        private int width;

        public PlacePhoto(int height, String photo_reference, int width) {
            this.height = height;
            this.photo_reference = photo_reference;
            this.width = width;
        }

        /**
         *
         * @return
         */
        public int getHeight() {
            return height;
        }

        /**
         *
         * @param height
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         *
         * @return
         */
        public String getPhotoReference() {
            return photo_reference;
        }

        /**
         *
         * @param photo_reference
         */
        public void setPhotoReference(String photo_reference) {
            this.photo_reference = photo_reference;
        }

        /**
         *
         * @return
         */
        public int getWidth() {
            return width;
        }

        /**
         *
         * @param width
         */
        public void setWidth(int width) {
            this.width = width;
        }
    }
}
