package com.Iriseplos.iriseplayer.player.filesystem;

import com.Iriseplos.iriseplayer.mp3agic.*;
import com.Iriseplos.iriseplayer.renderer.tools.TimeParser;
import javafx.beans.NamedArg;
import javafx.scene.image.Image;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Music {
    //音乐名称
    private String name;
    //音乐所属专辑名称
    private String album;
    //参与创作的艺术家名称
    private String artist;

    private int lengthSeconds;

    private int bitRates;

    private Image albumIcon;

    private File musicFile;

    private ID3v1 mp3ID3v1Tag;

    private ID3v2 mp3ID3v2Tag;

    private int bytesPerFrame;

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getBytesPerFrame(){
        return bytesPerFrame;
    }

    public enum InfoType{NAME,ALBUM,ARTIST,LENGTH}

    public Music(File inputFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        this.musicFile = inputFile;
        if(Objects.equals(MusicLoader.getFileExtension(musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(musicFile);
            this.bytesPerFrame = 4608;
            if(loadedMp3File.hasId3v2Tag()) {
                this.mp3ID3v2Tag = loadedMp3File.getId3v2Tag();
            } else if (loadedMp3File.hasId3v1Tag()) {
                this.mp3ID3v1Tag = loadedMp3File.getId3v1Tag();
            }
            if(mp3ID3v2Tag != null){
                this.name = mp3ID3v2Tag.getTitle();
                this.album = mp3ID3v2Tag.getAlbum();
                this.artist = mp3ID3v2Tag.getArtist();
            }else{
                if(mp3ID3v1Tag != null) {
                    this.name = mp3ID3v1Tag.getTitle();
                    this.album = mp3ID3v1Tag.getAlbum();
                    this.artist = mp3ID3v1Tag.getArtist();
                }
            }
        }else if(Objects.equals(MusicLoader.getFileExtension(musicFile), "wav")){
            this.bytesPerFrame = SoundFileFormat.getSoundFrameSizeInBits(inputFile);
            this.name = musicFile.getName();
        }
    }

    public Music() {
    }

    public File getMusicFile() {
        return musicFile;
    }
    public static String getMusicInfoForListShowing(File _musicFile, @NamedArg("NAME,ALBUM,ARTIST,LENGTH") InfoType type) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(type == InfoType.ALBUM){
            if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
                Mp3File loadedMp3File = new Mp3File(_musicFile);
                if (loadedMp3File.hasId3v2Tag()) {
                    return loadedMp3File.getId3v2Tag().getAlbum();
                } else if (loadedMp3File.hasId3v1Tag()) {
                    return loadedMp3File.getId3v1Tag().getAlbum();
                }else{
                    return null;
                }
            }else if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "wav")){
                return "未知专辑";
            }else{
                return null;
            }
        }else if(type == InfoType.NAME){
            if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
                Mp3File loadedMp3File = new Mp3File(_musicFile);
                if (loadedMp3File.hasId3v2Tag()) {
                    return loadedMp3File.getId3v2Tag().getTitle();
                } else if (loadedMp3File.hasId3v1Tag()) {
                    return loadedMp3File.getId3v1Tag().getTitle();
                }else{
                    return null;
                }
            }else if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "wav")){
                return _musicFile.getName();
            }else{
                return null;
            }
        }else if(type == InfoType.ARTIST){
            if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
                Mp3File loadedMp3File = new Mp3File(_musicFile);
                if (loadedMp3File.hasId3v2Tag()) {
                    return loadedMp3File.getId3v2Tag().getArtist();
                } else if (loadedMp3File.hasId3v1Tag()) {
                    return loadedMp3File.getId3v1Tag().getArtist();
                } else {
                    return null;
                }
            } else if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "wav")){
                return "未知艺术家";
            }else{
                return null;
            }
        }else if(type == InfoType.LENGTH){
            if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
                Mp3File loadedMp3File = new Mp3File(_musicFile);
                return TimeParser.parseSecondsToMMSS(loadedMp3File.getLengthInSeconds());
            }else if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "wav")){
                return TimeParser.parseSecondsToMMSS(SoundFileFormat.getSoundLengthInSeconds(_musicFile));
            }
        }else{
            return null;
        }
        return null;
    }
}
