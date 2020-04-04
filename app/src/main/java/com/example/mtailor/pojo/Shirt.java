package com.example.mtailor.pojo;

public class Shirt {

    String UID;
    String height;
    String chest;
    String front;
    String shoulder;
    String sleeves;
    String sleevesText;
    String collar;
    String pocket;
    String type;
    String notes;

    public Shirt(){}

    public Shirt(String UID, String height, String chest, String front, String shoulder, String sleeves, String sleevesText, String collar, String pocket, String type, String notes) {
        this.UID = UID;
        this.height = height;
        this.chest = chest;
        this.front = front;
        this.shoulder = shoulder;
        this.sleeves = sleeves;
        this.sleevesText = sleevesText;
        this.collar = collar;
        this.pocket = pocket;
        this.type = type;
        this.notes = notes;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getSleeves() {
        return sleeves;
    }

    public void setSleeves(String sleeves) {
        this.sleeves = sleeves;
    }

    public String getSleevesText() {
        return sleevesText;
    }

    public void setSleevesText(String sleevesText) {
        this.sleevesText = sleevesText;
    }

    public String getCollar() {
        return collar;
    }

    public void setCollar(String collar) {
        this.collar = collar;
    }

    public String getPocket() {
        return pocket;
    }

    public void setPocket(String pocket) {
        this.pocket = pocket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
