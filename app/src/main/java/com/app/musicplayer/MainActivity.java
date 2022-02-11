package com.app.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    final int PERMISSION_CODE = 100;
    RecyclerView recyclerView;
    FileHandler fileHandler;
    RecyclerViewAdapter ad;
    ArrayList<File> musicFiles = new ArrayList<>();
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        initRecyclerView();
        fileHandler = FileHandler.getInstance();
        musicFiles = fileHandler.getFileArrayList();
        System.out.println("this should print first");
        ad = new RecyclerViewAdapter(musicFiles);
        recyclerView.setAdapter(ad);
        ad.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                Intent playactivityIntent = new Intent(getApplicationContext(), PlayActivity.class);
                playactivityIntent.putExtra("musicFiles", musicFiles);
                playactivityIntent.putExtra("index", position);
                startActivity(playactivityIntent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow in your app.
                // Toast.makeText(getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();
                fileHandler.prepareMusicFiles(Environment.getExternalStorageDirectory());
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        ad.notifyDataSetChanged();
                        if (fileHandler.isFilePreparetionDone) {
                            handler.removeCallbacks(runnable);
                        } else {
                            handler.postDelayed(runnable, 600);

                        }

                    }
                };
                handler.postDelayed(runnable, 100);

            } else {
                //Permission denied
                Toast.makeText(getApplicationContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecor.setDrawable(getDrawable(R.drawable.devider));
        recyclerView.addItemDecoration(itemDecor);
    }
}
