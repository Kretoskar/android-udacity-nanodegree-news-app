package com.example.android.newsapp;

/**
 * Created by admin on 25.03.2018.
 */

public class News {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mDate;
    private String mUrl;

    public News(String title, String section, String url, String author, String date) {
        mTitle = title;
        mSection = section;
        mUrl = url;
        mAuthor = author;
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAuthor() {
        return mAuthor;
    }
}
