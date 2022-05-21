package com.example.newsapp;


public class News {

    private String mImageUrl;
    private String mNewsHeading;
    private String mWriter;
    private String mDateTime;
    private String mNewsType;
    private String mNewsUrl;

    public News(String imageUrl, String newsHeading, String writer, String dateTime, String newsType, String newsUrl) {
        mImageUrl = imageUrl;
        mNewsHeading = newsHeading;
        mWriter = writer;
        mDateTime = dateTime;
        mNewsType = newsType;
        mNewsUrl = newsUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getNewsHeading() {
        return mNewsHeading;
    }

    public String getWriter() {
        return mWriter;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public String getNewsType() {
        return mNewsType;
    }

    public String getNewsUrl() {
        return mNewsUrl;
    }

}
