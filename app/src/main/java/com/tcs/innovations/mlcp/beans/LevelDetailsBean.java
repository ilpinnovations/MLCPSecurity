package com.tcs.innovations.mlcp.beans;

/**
 * Created by abhi on 2/20/2016.
 */
public class LevelDetailsBean {

    private String levelName;
    private String occupiedSlots;
    private String freeSlots;
    private String levelId;
    private String slotName;

    public LevelDetailsBean() {
    }

    public LevelDetailsBean(String levelName, String occupiedSlots, String freeSlots,String levelId,String slotName) {
        this.levelName = levelName;
        this.occupiedSlots = occupiedSlots;
        this.freeSlots = freeSlots;
        this.levelId=levelId;
        this.slotName=slotName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(String occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public String getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(String freeSlots) {
        this.freeSlots = freeSlots;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }
}
