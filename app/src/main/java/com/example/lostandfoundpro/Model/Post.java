package com.example.lostandfoundpro.Model;

import java.util.Date;

public class Post extends PostId{

    private String Description,PostImg,User,CategoryValue,LocationValue;
    private Date Time;

    public String getDescription() {
        return Description;
    }

    public String getPostImg() {
        return PostImg;
    }

    public String getUser() {
        return User;
    }

    public String getCategoryValue() {
        return CategoryValue;
    }

    public String getLocationValue() {
        return LocationValue;
    }

    public Date getTime() {
        return Time;
    }
}
