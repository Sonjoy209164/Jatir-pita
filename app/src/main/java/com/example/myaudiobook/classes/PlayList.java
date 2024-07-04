package com.example.myaudiobook.classes;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class PlayList implements Serializable {
    private static final String DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/myaudiobook-5fa37.appspot.com/o/mujib_default_icon.jpg?alt=media&token=af0e50c3-d668-4e9d-a6fe-110fcc0b7c34";
    private String name, key, lastUpdated, imageUrl, author,details;

    public PlayList(String name, String key, String lastUpdated, String imageUrl, String author,String details) {
        this.name = name;
        this.key = key;
        this.lastUpdated = lastUpdated;
        this.imageUrl = imageUrl;
        this.author = author;
        this.details = details;
    }


    public PlayList() {
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public boolean isFullyEquals(PlayList item){
        return name.equals(item.name) &&
            key.equals(item.key) &&
            lastUpdated.equals(item.lastUpdated) &&
            imageUrl.equals(item.imageUrl) &&
            author.equals(item.author);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getImageUrl() {
        if(imageUrl == null || imageUrl.trim().isEmpty()){
            return DEFAULT_URL;
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
