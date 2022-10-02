package com.Iriseplos.iriseplayer.player.filesystem;

import java.io.File;
import java.io.FileWriter;

public class ListSaver {
    //要保存的音乐列表
    private String[] musicList;
    //单独方法: 获取列表
    public void setMusicListToSave(String[] filePaths){
        musicList = filePaths;
    }
    //保存
    public boolean save(){
        try{
            File targetFile = new File("userdata/lastList.txt");
            if(!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileWriter fw = new FileWriter(targetFile);
            fw.write("");
            fw.flush();
            for(String tmpFilePath : musicList){
                fw.append(tmpFilePath).append("\n");
            }
            fw.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //构造函数: 获取列表
    public ListSaver(String[] filePaths) {
        this.musicList = filePaths;
    }
    public ListSaver(){}
}