package com.example.myaudiobook.listener;

import com.example.myaudiobook.classes.AudioBook;

public interface AudioBookListener {
    void onPlayRequest(AudioBook audioBook);
    void onMessageFound(String message);
}
