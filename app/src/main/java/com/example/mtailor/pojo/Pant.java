package com.example.mtailor.pojo;

public class Pant {

    String UID;
    String height;
    String waist;
    String seat;
    String bottom;
    String thigh;
    String uttaar;
    String pocket;
    String plates;
    String notes;

    public Pant(){}

    public Pant(String UID, String height, String waist, String seat, String bottom, String thigh, String uttaar, String pocket, String plates, String notes) {
        this.UID = UID;
        this.height = height;
        this.waist = waist;
        this.seat = seat;
        this.bottom = bottom;
        this.thigh = thigh;
        this.uttaar = uttaar;
        this.pocket = pocket;
        this.plates = plates;
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

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getThigh() {
        return thigh;
    }

    public void setThigh(String thigh) {
        this.thigh = thigh;
    }

    public String getUttaar() {
        return uttaar;
    }

    public void setUttaar(String uttaar) {
        this.uttaar = uttaar;
    }

    public String getPocket() {
        return pocket;
    }

    public void setPocket(String pocket) {
        this.pocket = pocket;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
