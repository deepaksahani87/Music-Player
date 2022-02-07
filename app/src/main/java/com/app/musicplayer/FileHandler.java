package com.app.musicplayer;

import java.io.File;
import java.util.ArrayList;

public class FileHandler {

   static FileHandler getInstance(){
        return  new FileHandler();
    }
    public ArrayList<File> getMusicFiles(File file) {
        File files[] = file.listFiles();
        ArrayList<File> fileList = new ArrayList<File>();
        for (File item : files) {
            if (item.isDirectory() && !item.getName().startsWith(".") && !item.getName().equalsIgnoreCase("Android")) {
                fileList.addAll(getMusicFiles(item));
            } else {
                if (item.getName().endsWith(".mp3")) {
                    fileList.add(item);
                }
            }
        }
        return fileList;
    }

    String getFileName(File file){
        return file.getName();
    }
}
