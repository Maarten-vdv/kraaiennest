package com.kraaiennest.kraaiennestapp.main;

public abstract class Action {

    private String text;
    private int color;
    private String icon;

    public Action(String text, int color, String icon) {
        this.text = text;
        this.color = color;
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public abstract void activate();
}
