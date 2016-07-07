package com.application.material.takeacoffee.app.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CoffeePlace implements Serializable {
    public static final String COFFEE_PLACE_PHOTO_REFERENCE_KEY = "PHOTO_REFERENCE_KEY";
    public static String COFFEE_PLACE_ID_KEY = "PLACE_ID_KEY";
    public static String COFFEE_PLACE_NAME_KEY = "PLACE_NAME_KEY";
    public static String COFFEE_MACHINE_OBJ_KEY = "COFFEE_MACHINE_OBJ";
    public static String COFFEE_PLACE_LATLNG_REFERENCE_KEY = "COFFEE_PLACE_LATLNG_REFERENCE_KEY";

    public static final String CAFE_PLACE_TYPE = "cafe";
    public static final String BAR_PLACE_TYPE = "bar";
    public static final String PLACE_RANKBY = "distance";

    private PageToken pageTokenRef;

    @SerializedName("place_id")
    private String id;
	private String name;
    @SerializedName("vicinity")
	private String address;
    private int rating;
    private ArrayList<PlacePhoto> photos;
    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }

    /**
     *
     * @param place_id
     * @param name
     * @param vicinity
     * @param rating
     */
    public CoffeePlace(String place_id, final String name, String vicinity, int rating,
                       ArrayList<PlacePhoto> photos, PageToken pageToken, Geometry geometry) {
        this.id = place_id;
        this.rating = rating;
		this.name = name;
		this.address = vicinity;
        this.photos = photos;
        this.pageTokenRef = pageToken;
        this.geometry = geometry;
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
    public String getPhotoReference() {
        return photos != null ? photos.get(0).getPhotoReference() : null;
    }

    /**
     *
     * @return
     */
    public int getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     */
    public PageToken getPageToken() {
        return pageTokenRef;
    }

    /**
     *
     * @param pageToken
     */
    public void setPageToken(PageToken pageToken) {
        this.pageTokenRef = pageToken;
    }

    /**
     * static class to handle photo
     */
    public static class PageToken implements Serializable {
        private static String token;
        private static PageToken instance;

        private PageToken(String tkn) {
            token = tkn;
        }

        /**
         *
         * @param tkn
         * @return
         */
        public static PageToken getInstance(String tkn) {
            token = tkn;
            return instance == null ?
                    instance = new PageToken(token) : instance;
        }

        /**
         *
         * @return
         */
        public String getToken() {
            return token;
        }
    }


    /**
     * static class to handle photo
     */
    public static class Geometry implements Serializable {
        Location location;

        /**
         *
         * @return
         */
        public Location getLocation() {
            return location;
        }

        /**
         *
         * @param location
         */
        public Geometry(Location location) {
//            Log.e("TAG", location.toString());
            this.location = location;
        }

        /**
         *
         */
        public class Location implements Serializable {
            private final float lat;
            private final float lng;

            /**
             *
             * @param lat
             * @param lng
             */
            public Location(float lat, float lng) {
                this.lat = lat;
                this.lng = lng;
            }

            /**
             *
             * @return
             */
            public float getLat() {
                return lat;
            }

            /**
             *
             * @return
             */
            public float getLng() {
                return lng;
            }
        }
    }
    /**
     * static class to handle photo
     */
    public static class PlacePhoto implements Serializable {
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
