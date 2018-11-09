package com.facundoprecentado.howlonguntilchristmas;

public class BackgroundRecyclerViewItem {

    // Background name
    private String backgroundName;

    // Background image resource id.
    private int backgroundImageId;

    public BackgroundRecyclerViewItem(String backgroundName, int backgroundImageId) {
        this.backgroundName = backgroundName;
        this.backgroundImageId = backgroundImageId;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public int getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(int backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }
}
