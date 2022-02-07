package com.adarsh45.mobitailorapp.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Pant {

   private String id, lastUpdateDate, height, waist, seat, bottom, knee,
                  thigh1, thigh2, seatRound, seatUtaar, notes;
//   radio btn inputs
   private String pantType, pantPocket, pantPlates, pantSide, pantStitch, pantBackPocket;

   public Pant(){}

   public Pant(String id, String lastUpdateDate, String height, String waist, String seat, String bottom, String knee, String thigh1, String thigh2, String seatRound, String seatUtaar, String notes, String pantType, String pantPocket, String pantPlates, String pantSide, String pantStitch, String pantBackPocket) {
      this.id = id;
      this.lastUpdateDate = lastUpdateDate;
      this.height = height;
      this.waist = waist;
      this.seat = seat;
      this.bottom = bottom;
      this.knee = knee;
      this.thigh1 = thigh1;
      this.thigh2 = thigh2;
      this.seatRound = seatRound;
      this.seatUtaar = seatUtaar;
      this.notes = notes;
      this.pantType = pantType;
      this.pantPocket = pantPocket;
      this.pantPlates = pantPlates;
      this.pantSide = pantSide;
      this.pantStitch = pantStitch;
      this.pantBackPocket = pantBackPocket;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getLastUpdateDate() {
      return lastUpdateDate;
   }

   public void setLastUpdateDate(String lastUpdateDate) {
      this.lastUpdateDate = lastUpdateDate;
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

   public String getKnee() {
      return knee;
   }

   public void setKnee(String knee) {
      this.knee = knee;
   }

   public String getThigh1() {
      return thigh1;
   }

   public void setThigh1(String thigh1) {
      this.thigh1 = thigh1;
   }

   public String getThigh2() {
      return thigh2;
   }

   public void setThigh2(String thigh2) {
      this.thigh2 = thigh2;
   }

   public String getSeatRound() {
      return seatRound;
   }

   public void setSeatRound(String seatRound) {
      this.seatRound = seatRound;
   }

   public String getSeatUtaar() {
      return seatUtaar;
   }

   public void setSeatUtaar(String seatUtaar) {
      this.seatUtaar = seatUtaar;
   }

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public String getPantType() {
      return pantType;
   }

   public void setPantType(String pantType) {
      this.pantType = pantType;
   }

   public String getPantPocket() {
      return pantPocket;
   }

   public void setPantPocket(String pantPocket) {
      this.pantPocket = pantPocket;
   }

   public String getPantPlates() {
      return pantPlates;
   }

   public void setPantPlates(String pantPlates) {
      this.pantPlates = pantPlates;
   }

   public String getPantSide() {
      return pantSide;
   }

   public void setPantSide(String pantSide) {
      this.pantSide = pantSide;
   }

   public String getPantStitch() {
      return pantStitch;
   }

   public void setPantStitch(String pantStitch) {
      this.pantStitch = pantStitch;
   }

   public String getPantBackPocket() {
      return pantBackPocket;
   }

   public void setPantBackPocket(String pantBackPocket) {
      this.pantBackPocket = pantBackPocket;
   }
}
