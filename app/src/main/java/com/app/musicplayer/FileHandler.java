package com.app.musicplayer;

import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

public class FileHandler {

    public ArrayList<File> fileArrayList = new ArrayList<>();
    private Thread thread;

    boolean isFilePreparetionDone=false;
    static FileHandler getInstance() {
        return new FileHandler();
    }

    public void prepareMusicFiles(File file) {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getFileList(file);
                isFilePreparetionDone = true;
                thread.interrupt();
            }
        });
        thread.start();
    }

    ArrayList<File> getFileList(File file) {
        ArrayList<File> files = new ArrayList<>();
        File[] fileArr = file.listFiles();
        if (fileArr != null) {
            for (File item : fileArr) {
                if (item.isDirectory() && !item.getName().startsWith(".") && !item.getName().equalsIgnoreCase("Android")) {
                    files.addAll(getFileList(item));
                } else {
                    if (item.getName().endsWith(".mp3")) {
                        files.add(item);
                        fileArrayList.add(item);
                    }
                }
            }
        }
        return files;
    }
    ArrayList<File> getFileArrayList(){
        return fileArrayList;
    }

}
