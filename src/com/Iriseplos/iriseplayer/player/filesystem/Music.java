package com.Iriseplos.iriseplayer.player.filesystem;

import com.Iriseplos.iriseplayer.mp3agic.*;
import com.Iriseplos.iriseplayer.renderer.tools.TimeParser;
import javafx.scene.image.Image;

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

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public Music(File inputFile) throws InvalidDataException, UnsupportedTagException, IOException {
        this.musicFile = inputFile;
        if(Objects.equals(MusicLoader.getFileExtension(musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(musicFile);
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
        }
    }

    public Music() {
    }

    public File getMusicFile() {
        return musicFile;
    }
    public static String getMusicNameForListShowing(File _musicFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(_musicFile);
            if (loadedMp3File.hasId3v2Tag()) {
                return loadedMp3File.getId3v2Tag().getTitle();
            } else if (loadedMp3File.hasId3v1Tag()) {
                return loadedMp3File.getId3v1Tag().getTitle();
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    public static String getMusicArtistForListShowing(File _musicFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(_musicFile);
            if (loadedMp3File.hasId3v2Tag()) {
                return loadedMp3File.getId3v2Tag().getArtist();
            } else if (loadedMp3File.hasId3v1Tag()) {
                return loadedMp3File.getId3v1Tag().getArtist();
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    public static String getMusicAlbumForListShowing(File _musicFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(_musicFile);
            if (loadedMp3File.hasId3v2Tag()) {
                return loadedMp3File.getId3v2Tag().getAlbum();
            } else if (loadedMp3File.hasId3v1Tag()) {
                return loadedMp3File.getId3v1Tag().getAlbum();
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    public static String getMusicSecondsLengthForListShowing(File _musicFile) throws InvalidDataException, UnsupportedTagException, IOException {
        if(Objects.equals(MusicLoader.getFileExtension(_musicFile), "mp3")) {
            Mp3File loadedMp3File = new Mp3File(_musicFile);
            long lengthInSeconds = loadedMp3File.getLengthInSeconds();
            return TimeParser.parseSecondsTommss(lengthInSeconds);
        }else{
            return "未知长度";
        }
    }
}
