package com.application.material.takeacoffee.app.models;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class City implements Serializable {
    public static String CITIES_TYPE = "(cities)";
    public static String CITIES_LANGUAGE = "it";
    private static String SELECTED_COUNTRY = "Italy";

    private String description;
    private String reference;
    private Term[] terms;
    private String[] types;

    /**
     *
     * @param description
     * @param reference
     * @param terms
     * @param types
     */
    public City(String description, String reference, Term[] terms, String[] types) {
        this.description = description;
        this.reference = reference;
        this.terms = terms;
        this.types = types;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getReference() {
        return reference;
    }

    /**
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     *
     * @return
     */
    public Term[] getTerms() {
        return terms;
    }

    /**
     *
     * @param terms
     */
    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    /**
     *
     * @return
     */
    public String[] getTypes() {
        return types;
    }

    /**
     *
     * @param types
     */
    public void setTypes(String[] types) {
        this.types = types;
    }

    /**
     *
     */
    private class Term implements Serializable {
        private int offset;
        private String value;

        /**
         *
         * @param offset
         * @param value
         */
        private Term(int offset, String value) {
            this.offset = offset;
            this.value = value;
        }

        /**
         *
         * @return
         */
        public int getOffset() {
            return offset;
        }

        /**
         *
         * @param offset
         */
        public void setOffset(int offset) {
            this.offset = offset;
        }

        /**
         *
         * @return
         */
        public String getValue() {
            return value;
        }

        /**
         *
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     *
     * @param list
     * @return
     */
    public static String[] getArrayFromList(ArrayList<City> list) {
        final String[] stringArray = new String[list.size()];
        int i = 0;
        for (City temp : list) {
//            if (temp.getTerms().length >= 3 &&
//                    temp.getTerms()[2].getValue().equals(SELECTED_COUNTRY)) {
                stringArray[i++] = temp.getTerms()[0].getValue();
//            }
        }
        return stringArray;
    }

}
