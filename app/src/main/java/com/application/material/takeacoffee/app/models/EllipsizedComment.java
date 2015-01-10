package com.application.material.takeacoffee.app.models;

/**
 * Created by davide on 26/12/14.
 */
public class EllipsizedComment {
    private boolean isEllipsized;
    private boolean isHidden;
    private String plainComment;
    private String ellipsizedComment;
    public static int MAX_CHAR_COMMENT = 22;


    public EllipsizedComment(String plainComment, String ellipsizedComment, boolean isEllipsized) {
        this.plainComment = plainComment;
        this.ellipsizedComment = ellipsizedComment;
        this.isEllipsized = isEllipsized;
        this.isHidden = true;
    }

    public boolean isEllipsized() {
        return isEllipsized;
    }

    public void setEllipsized(boolean isEllipsized) {
        this.isEllipsized = isEllipsized;
    }

    public String getPlainComment() {
        return plainComment;
    }

    public void setPlainComment(String plainComment) {
        this.plainComment = plainComment;
    }

    public String getEllipsizedComment() {
        return ellipsizedComment;
    }

    public void setEllipsizedComment(String ellipsizedComment) {
        this.ellipsizedComment = ellipsizedComment;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
}
