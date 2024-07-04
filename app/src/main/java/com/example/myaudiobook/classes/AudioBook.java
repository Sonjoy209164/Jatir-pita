package com.example.myaudiobook.classes;

import androidx.annotation.Keep;

@Keep
public class AudioBook {

    private String playlistKey,key, name, author, url,duration;
    private Long lastPlayed = 0L;

    public AudioBook(String playlistKey,String key, String name, String author, String url,String duration) {
        this.playlistKey = playlistKey;
        this.key = key;
        this.name = name;
        this.author = author;
        this.url = url;
        this.duration = duration;
    }


    public AudioBook() {
    }

    public String getDuration() {
        if(duration == null){
            return "Not found";
        }
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public boolean isFullyEquals(AudioBook item){
        return key.equals(item.key) &&
            name.equals(item.name) &&
            author.equals(item.author) &&
            url.equals(item.url);
    }

    public String getPlaylistKey() {
        return playlistKey;
    }

    public void setPlaylistKey(String playlistKey) {
        this.playlistKey = playlistKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public boolean isUrlValid(){
        return url != null && !url.trim().isEmpty();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
