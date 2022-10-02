package com.Iriseplos.iriseplayer.agent;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.MusicPlayer;
import com.Iriseplos.iriseplayer.player.PlayingStatus;
import com.Iriseplos.iriseplayer.player.filesystem.ListSaver;
import com.Iriseplos.iriseplayer.player.filesystem.MusicList;
import com.Iriseplos.iriseplayer.player.filesystem.MusicLoader;
import com.Iriseplos.iriseplayer.renderer.Start;
import javafx.scene.image.Image;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Agent {
    private MusicPlayer musicPlayer = new MusicPlayer();

    private ListSaver listSaver = new ListSaver();

    private boolean isSettedNext;
    public Agent() throws LineUnavailableException {}
    public void toPause(){
        musicPlayer.pauseMusic();
    }
    /*public void toContinue(){
        musicPlayer.continueMusic();
    }*/
    public void toStop() throws Exception {
        musicPlayer.stopMusic();
    }
    /*public void toStart() throws Exception {
        musicPlayer.start(false,0);
    }*/
    public PlayingStatus agentCheckPlayingStatus(){
        return musicPlayer.checkPlayingStatus();
    }
    public void controlStatus() throws Exception {
        PlayingStatus st = musicPlayer.checkPlayingStatus();
        if(st==PlayingStatus.INITIAL_LOAD){
            musicPlayer.start(true,0);
        }else if(st==PlayingStatus.IS_PLAYING){
            musicPlayer.pauseMusic();
        }else if(st==PlayingStatus.PAUSED){
            musicPlayer.continueMusic();
        }else if(st==PlayingStatus.STOPPED){
            musicPlayer.start(true,0);
        }
    }
    /*
        三种状态:
        1.初次播放
        2.正在播放
        3.已经开始播放但处于暂停状态
     */
    public File agentSetMusic(int indexInList) throws Exception {
        if (musicPlayer.checkPlayingStatus() == PlayingStatus.IS_PLAYING || musicPlayer.checkPlayingStatus() == PlayingStatus.PAUSED) {
            musicPlayer.stopMusic();
            musicPlayer = new MusicPlayer(MusicList.generateMusic(indexInList));
            System.gc();
            return musicPlayer.getCurrentPlayingFile();
        }else if(musicPlayer.checkPlayingStatus() == PlayingStatus.STOPPED) {
            musicPlayer = new MusicPlayer(MusicList.generateMusic(indexInList));
            System.gc();
            return musicPlayer.getCurrentPlayingFile();
        }
        return null;
    }
    public long agentGetTotalLength(File mF) throws Exception {
        if(Objects.equals(MusicLoader.getFileExtension(mF), "wav")) {
            return MusicLoader.getSoundLengthWAV(mF);
        }else if(Objects.equals(MusicLoader.getFileExtension(mF), "mp3")){
            return MusicLoader.getSoundFrameLengthMP3(mF);
        }else{
            return 0;
        }
    }
    public long agentGetPlayedLength() throws InvalidDataException, UnsupportedTagException, IOException {
        if(Objects.equals(MusicLoader.getFileExtension(musicPlayer.getCurrentPlayingFile()), "wav")) {
            return musicPlayer.getPlayedBytes();
        }else if(Objects.equals(MusicLoader.getFileExtension(musicPlayer.getCurrentPlayingFile()), "mp3")){
            return musicPlayer.getPlayedFramesMp3();
        }else{
            return 0;
        }
    }
    public Image agentGetAlbumIcon() throws InvalidDataException, UnsupportedTagException, IOException {
        return MusicLoader.getMusicAlbumIcon(musicPlayer.getCurrentPlayingFile());
    }

    public String agentGetCurrentMusicName(){
        return musicPlayer.getCurrentPlayingMusic().getName();
    }

    public String agentGetCurrentMusicArtist(){
        return musicPlayer.getCurrentPlayingMusic().getArtist();
    }
    public String agentGetCurrentMusicAlbum(){
        return musicPlayer.getCurrentPlayingMusic().getAlbum();
    }

    //返回布尔值表示是否添加成功
    public boolean agentAddMusic(File selectedFile){
        return MusicList.addMusic(selectedFile.getAbsolutePath());
    }

    public String agentRemoveMusic(int index){
        return MusicList.removeMusic(index);
    }

    public void agentNext() throws Exception {
        isSettedNext = false;
        for(String musicItemName : MusicList.getMusicList()){
            String currentName = musicPlayer.getCurrentPlayingFile().getAbsolutePath();
            if(currentName.equals(musicItemName) && !isSettedNext){
                if(MusicList.getMusicList().indexOf(currentName) == MusicList.getTotal()-1) {
                    agentSetMusic(0);
                }else {
                    agentSetMusic(MusicList.getMusicList().indexOf(currentName) + 1);
                }
                isSettedNext = true;
            }
        }
        isSettedNext = false;
    }
    public void agentLast() throws Exception {
        isSettedNext = false;
        for(String musicItemName : MusicList.getMusicList()){
            String currentName = musicPlayer.getCurrentPlayingFile().getAbsolutePath();
            if(currentName.equals(musicItemName) && !isSettedNext){
                if(MusicList.getMusicList().indexOf(currentName) == 0) {
                    agentSetMusic(MusicList.getTotal()-1);
                }else {
                    agentSetMusic(MusicList.getMusicList().indexOf(currentName) - 1);
                }
                isSettedNext = true;
            }
        }
        isSettedNext = false;
    }
    public void agentControlVolume(float delta){
        musicPlayer.controlVolume(delta);
    }
    public void agentSetAutoChanged(){
        Start.getStartUpWindow().getPlayerWindow().autoChange();
    }
    public void agentChangePosition(double playPercentage) throws Exception {
        musicPlayer.changePosition(playPercentage);
    }
    public long agentGetTotalByteLength(File mp3File) throws Exception {
        return MusicLoader.getTotalByteLength(mp3File);
    }
    public File agentGetCurrentPlayingFile(){
        return musicPlayer.getCurrentPlayingFile();
    }
    public void agentSaveMusicList(){
        listSaver.setMusicListToSave(agentGetMusicList().toArray(new String[0]));
        listSaver.save();
    }
    public String agentGetMusicLengthTime(File musicFile) throws InvalidDataException, UnsupportedTagException, IOException {
        return MusicList.getMusicLengthInSeconds(musicFile);
    }
    public ArrayList<String> agentGetMusicList(){
        return MusicList.getMusicList();
    }

    //暂不为列表显示启用代理类
    /*public String agentGetMusicName(){
        return MusicList.getMusicName();
    }

    public String agentGetMusicArtist(){
        return MusicList.getMusicArtist();
    }
    public String agentGetMusicAlbum(){
        return MusicList.getMusicAlbum();
    }*/
}
