package com.Iriseplos.iriseplayer.player.filesystem;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicList {
    private static ArrayList<String> musicPaths = new ArrayList<>();
    public static int currentTotal = 0;

    public static boolean addMusic(String fileName) {
        currentTotal++;
        return MusicList.musicPaths.add(fileName);
    }

    public static String removeMusic(int index){
        currentTotal--;
        return MusicList.musicPaths.remove(index);
    }

    public static Music generateMusic(int indexInList) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        //currentIndex++;
        return new Music(new File(musicPaths.get(indexInList)));
    }
    public static String getMusicName(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知标题";
        }else{
            return Music.getMusicInfoForListShowing(sourceFile, Music.InfoType.NAME);
        }
    }
    public static String getMusicArtist(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知艺术家";
        }else{
            return Music.getMusicInfoForListShowing(sourceFile, Music.InfoType.ARTIST);
        }
    }
    public static String getMusicAlbum(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知专辑";
        }else{
            return Music.getMusicInfoForListShowing(sourceFile, Music.InfoType.ALBUM);
        }
    }
    public static String getMusicLengthInSeconds(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(musicPaths.size() == 0 || sourceFile == null) {
            return "未知时长";
        }else{
            return Music.getMusicInfoForListShowing(sourceFile, Music.InfoType.LENGTH);
        }
    }

    public static int getTotal(){
        return currentTotal;
    }

    public static ArrayList<String> getMusicList(){
        return musicPaths;
    }
}
