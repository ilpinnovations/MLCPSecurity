package com.tcs.innovations.mlcp.beans;

/**
 * Created by abhi on 2/20/2016.
 */

public class DefaulterVehiclesBean {

    private String floorName;
    private String slotName;
    private String vehicleNumber;
    private String parkingTime;


    public DefaulterVehiclesBean() {
    }

    public DefaulterVehiclesBean(String floorName, String slotName, String vehicleNumber, String parkingTime) {
        this.floorName = floorName;
        this.slotName = slotName;
        this.vehicleNumber = vehicleNumber;
        this.parkingTime = parkingTime;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }


    public String getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(String parkingTime) {
        this.parkingTime = parkingTime;
    }
}
