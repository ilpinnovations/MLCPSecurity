package com.tcs.innovations.mlcp.beans;

/**
 * Created by abhi on 2/20/2016.
 */
public class OccupiedSlotsBean {

    private String slotName;
    private String vehicleNumber;
    private String slotId;
    public OccupiedSlotsBean() {
    }

    public OccupiedSlotsBean(String slotName, String vehicleNumber,String slotId) {
        this.slotName = slotName;
        this.vehicleNumber = vehicleNumber;
        this.slotId=slotId;
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

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}
