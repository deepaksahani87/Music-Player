package com.app.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class mPlayer {
    private Uri path;
    private Context context;
    private MediaPlayer mediaPlayer ;
    public mPlayer(Context c,String filepath){
        path = Uri.parse(filepath);
        context = c;
        mediaPlayer = MediaPlayer.create(context,path);
    }

    void play(){
        mediaPlayer.start();
    }
    void pause(){
        mediaPlayer.pause();
    }

    void stop(){
        mediaPlayer.stop();
        mediaPlayer.release();

    }

    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    int getDuration(){
        return  mediaPlayer.getDuration();
    }
    int getCurrentPosition(){
        return  mediaPlayer.getCurrentPosition();
    }
    void seekTo(int mili){
        mediaPlayer.seekTo(mili);
    }


}
