package com.app.musicplayer;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity{
    private mPlayer mediaplayer;
    private ImageView play_btn,prev_btn,next_btn,repeat_btn,shuffle_btn;
    private ImageView coverImage;
    private TextView Duration,currentPosition,musicTitle;
    private boolean isMediaPlaying = false;
    private int index;
    private ArrayList<File> fileArrayList;
    private SeekBar seekBar;

    private final Handler handler= new Handler();
    private Runnable runnable;
    int repeatCount = 0;
    boolean isSuffleOn = false;

    String APP_CONFIG_SHARED_PREFS = "app_config";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        play_btn = findViewById(R.id.play);
        prev_btn = findViewById(R.id.prev);
        next_btn = findViewById(R.id.next);
        repeat_btn = findViewById(R.id.repeat);
        shuffle_btn = findViewById(R.id.shuffle);
        coverImage = findViewById(R.id.music_cover);
        Duration = findViewById(R.id.duration);
        currentPosition = findViewById(R.id.startTime);
        seekBar= findViewById(R.id.seekBar);
        musicTitle = findViewById(R.id.musicTitle);


        fileArrayList = (ArrayList<File>) getIntent().getSerializableExtra("musicFiles");
        index =getIntent().getIntExtra("index",0);
        initMediaPlayer(fileArrayList.get(index).getAbsolutePath());
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"play btn",Toast.LENGTH_SHORT).show();
                if(isMediaPlaying) {
                    mediaplayer.pause();
                    play_btn.setImageResource(R.drawable.play);
                    isMediaPlaying = false;
                    handler.removeCallbacks(runnable);
                }
                else{
                    mediaplayer.play();
                    play_btn.setImageResource(R.drawable.pause);
                    isMediaPlaying = true;
                    handler.postDelayed(runnable,0);

                }
            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"prev btn",Toast.LENGTH_SHORT).show();
                if(isSuffleOn){
                    index = randomNumber();
                }
                else{
                    if(index<=0){
                        index = fileArrayList.size()-1;
                    }
                    else{
                        index = index-1;
                    }
                }

                mediaplayer.stop();
                isMediaPlaying = false;
                initMediaPlayer(fileArrayList.get(index).getAbsolutePath());
                play_btn.callOnClick();

            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"next btn",Toast.LENGTH_SHORT).show();
                if(isSuffleOn){
                    index = randomNumber();
                }
                else{
                    if(index==fileArrayList.size()-1){
                        index = 0;
                    }
                    else{
                        index = index+1;
                    }

                }

                mediaplayer.stop();
                isMediaPlaying = false;
                initMediaPlayer(fileArrayList.get(index).getAbsolutePath());
                play_btn.callOnClick();
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"repeat btn",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences(APP_CONFIG_SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch(repeatCount){
                    case 0:
                        repeatCount+=1;
                        repeat_btn.setImageResource(R.drawable.repeat_single);
                        editor.putInt("repeatCount",repeatCount);
                        break;
                    case 1:
                        repeatCount+=1;
                        repeat_btn.setImageResource(R.drawable.repeat_all);
                        editor.putInt("repeatCount",repeatCount);
                        break;
                    case 2:
                        repeatCount = 0;
                        repeat_btn.setImageResource(R.drawable.repeat);
                        editor.putInt("repeatCount",repeatCount);
                        break;
                }
                editor.commit();
            }
        });

        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"shuffle btn",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences(APP_CONFIG_SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isSuffleOn){
                    editor.putBoolean("isShuffleOn",false);
                    shuffle_btn.setImageResource(R.drawable.shuffle);
                    isSuffleOn = false;
                }
                else {
                    editor.putBoolean("isShuffleOn",true);
                    shuffle_btn.setImageResource(R.drawable.shuffle_active);
                    isSuffleOn = true;
                }
                editor.commit();

            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                currentPosition.setText(milliToHMS(mediaplayer.getCurrentPosition()));
                seekBar.setMax(mediaplayer.getDuration());
                seekBar.setProgress(mediaplayer.getCurrentPosition());
                if(!mediaplayer.isPlaying() ){
                    play_btn.setImageResource(R.drawable.play);
                    seekBar.setProgress(0);
                    currentPosition.setText("00:00");
                    isMediaPlaying = false;
                    handler.removeCallbacks(runnable);
                    switch(repeatCount){
                        case 1:
                            play_btn.callOnClick();
                            break;
                        case 2:
                            next_btn.callOnClick();
                            break;
                    }
                }
                handler.postDelayed(runnable,100);
            }
        };
        play_btn.callOnClick();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    mediaplayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    void initMediaPlayer(String filePath){
        mediaplayer = new mPlayer(this,filePath);
        setCoverImage(filePath);
        Duration.setText(milliToHMS(mediaplayer.getDuration()));
        musicTitle.setText(new File(filePath).getName());
        musicTitle.setSelected(true);
        SharedPreferences sharedPreferences = getSharedPreferences(APP_CONFIG_SHARED_PREFS,MODE_PRIVATE);
        isSuffleOn = sharedPreferences.getBoolean("isShuffleOn",false);
        if (isSuffleOn){
            shuffle_btn.setImageResource(R.drawable.shuffle_active);
        }
        else {
            shuffle_btn.setImageResource(R.drawable.shuffle);
        }

        repeatCount = sharedPreferences.getInt("repeatCount",0);
        switch(repeatCount){
            case 0:
                repeat_btn.setImageResource(R.drawable.repeat);
                break;
            case 1:
                repeat_btn.setImageResource(R.drawable.repeat_single);
                break;
            case 2:
                repeat_btn.setImageResource(R.drawable.repeat_all);
                break;
        }


    }



    public void setCoverImage(String url)
    {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);

        byte [] data = mmr.getEmbeddedPicture();

        // convert the byte array to a bitmap
        if (data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            coverImage.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            coverImage.setImageResource(R.drawable.music_cover); //any default cover resourse file
        }
    }

    public String milliToHMS(int millis)
    {
        String hms="";
        if (millis / (1000 * 60) >= 60)
        {
            hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }
        else
        {
            hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }
        return hms;
    }

    public int randomNumber()
    {
        int result;
        Random rand=new Random();
        result = rand.nextInt(fileArrayList.size());
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mediaplayer.stop();
        startService(new Intent(this,BackgroundPlaybackService.class));
        handler.removeCallbacks(runnable);

    }

    void startForegroundService(){
        Notification notification = new Notification();
    }
}
