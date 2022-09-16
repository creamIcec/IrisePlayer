package com.Iriseplos.iriseplayer.player.filesystem;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicList {
    private static Music music;
    private static ArrayList<String> musicPaths = new ArrayList<>();

    public static ArrayList<String> getMusicPaths() {
        return musicPaths;
    }

    public static int currentTotal = 0;

    private static int currentPlayingIndex = 0;

    public static boolean addMusic(String fileName) {
        currentTotal++;
        return MusicList.musicPaths.add(fileName);
    }

    public static String removeMusic(int index){
        currentTotal--;
        return MusicList.musicPaths.remove(index);
    }

    public static Music generateMusic(int indexInList) throws InvalidDataException, UnsupportedTagException, IOException {
        music = new Music(new File(musicPaths.get(indexInList)));
        //currentIndex++;
        return music;
    }
    public static String getMusicName(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知标题";
        }else{
            return Music.getMusicNameForListShowing(sourceFile);
        }
    }
    public static String getMusicArtist(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知艺术家";
        }else{
            return Music.getMusicArtistForListShowing(sourceFile);
        }
    }
    public static String getMusicAlbum(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知专辑";
        }else{
            return Music.getMusicAlbumForListShowing(sourceFile);
        }
    }
    public static String getMusicLengthInSeconds(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知时长";
        }else{
            return Music.getMusicSecondsLengthForListShowing(sourceFile);
        }
    }

    /*public static setCurrentPlayingIndex(int index){
        currentPlayingIndex = index;
    }*/

    public static int getTotal(){
        return currentTotal;
    }

    public static ArrayList<String> getMusicList(){
        return musicPaths;
    }
}
