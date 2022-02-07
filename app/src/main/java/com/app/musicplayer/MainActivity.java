package com.app.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FileHandler fileHandler;
    RecyclerViewAdapter ad;
    final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    final int PERMISSION_CODE = 100;
    ArrayList<File> musicFiles = new ArrayList<>();
    ArrayList<String> musicFilesName= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        initRecyclerView();
        fileHandler = FileHandler.getInstance();
        ad = new RecyclerViewAdapter(musicFilesName);
        recyclerView.setAdapter(ad);
        ad.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                Intent playactivityIntent = new Intent(getApplicationContext(),PlayActivity.class);
                playactivityIntent.putExtra("musicFiles",musicFiles);
                playactivityIntent.putExtra("index",position);
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
                musicFiles = fileHandler.getMusicFiles(Environment.getExternalStorageDirectory());
                for (int i = 0; i < musicFiles.size(); i++) {
                    musicFilesName.add(fileHandler.getFileName(musicFiles.get(i)));
                    ad.notifyDataSetChanged();
                }



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
