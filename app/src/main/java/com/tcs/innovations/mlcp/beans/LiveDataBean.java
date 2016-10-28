package com.tcs.innovations.mlcp.beans;

/**
 * Created by abhi on 2/20/2016.
 */
public class LiveDataBean {

    private String floorName;
    private String slotName;
    private String vehicleNumber;


    public LiveDataBean() {
    }

    public LiveDataBean(String floorName, String slotName, String vehicleNumber) {
        this.floorName = floorName;
        this.slotName = slotName;
        this.vehicleNumber = vehicleNumber;

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

}
