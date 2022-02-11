package com.app.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class mPlayer {

    private Context context;
    private MediaPlayer mediaPlayer;

    public mPlayer(Context c, String filepath) {
        context = c;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(context, "Error Occurred While Playing", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void play() {
        mediaPlayer.start();
    }

    void pause() {
        mediaPlayer.pause();
    }

    void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();

    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    void seekTo(int mili) {
        mediaPlayer.seekTo(mili);
    }


}
