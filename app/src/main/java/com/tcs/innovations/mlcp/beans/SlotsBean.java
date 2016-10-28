package com.tcs.innovations.mlcp.beans;

/**
 * Created by abhi on 2/20/2016.
 */
public class SlotsBean {
    private String slotName;
    private String slotId;

    public SlotsBean() {
    }

    public SlotsBean(String slotName, String slotId) {
        this.slotName = slotName;
        this.slotId = slotId;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}
