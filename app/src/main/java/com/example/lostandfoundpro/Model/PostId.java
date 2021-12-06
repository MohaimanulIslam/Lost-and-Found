package com.example.lostandfoundpro.Model;

import com.google.firebase.firestore.Exclude;

import javax.annotation.Nullable;

public class PostId {
    @Exclude
    public String PostId;

    public <T extends PostId> T withId(@Nullable final String id){
        this.PostId = id;
        return (T) this;
    }

}
