package com.example.yu;

import android.net.Uri;

// MoodItem.java
public class MoodItem {

    private int imageResource;
    private String moodName;
    String text;
    public MoodItem(int imageResource, String moodName) {
        this.imageResource = imageResource;
        this.moodName = moodName;

    }

    public int getImageResource() {
        return imageResource;
    }

    public String getMoodName() {
        return moodName;
    }
    public String getText() {

        return text;
    }
}
