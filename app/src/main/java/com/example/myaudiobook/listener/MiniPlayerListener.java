package com.example.myaudiobook.listener;

public interface MiniPlayerListener {

    void onPaused();
    void onPlayed();

    void showMiniPlayer();
    void hideMiniPlayer();
    void setBookName(String name);
    void updateTimeAndProgress(String formattedTime, int progress);
}
