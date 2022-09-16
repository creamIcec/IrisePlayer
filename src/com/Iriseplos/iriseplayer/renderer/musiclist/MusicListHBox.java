package com.Iriseplos.iriseplayer.renderer.musiclist;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.filesystem.MusicList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;

public class MusicListHBox {
    Label musicName = new Label();
    Label artist = new Label();

    //TODO: 显示音乐时长
    Label timeSpan = new Label();

    Label album = new Label();
    public HBox getNewMusicHBox(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException {
        musicName = new Label(MusicList.getMusicName(sourceFile));
        artist = new Label(MusicList.getMusicArtist(sourceFile));
        album = new Label(MusicList.getMusicAlbum(sourceFile));
        timeSpan = new Label(MusicList.getMusicLengthInSeconds(sourceFile));
        musicName.setMaxWidth(150);
        artist.setMaxWidth(150);
        album.setMaxWidth(150);
        /*musicName.setText("未知标题");
        artist.setText("未知艺术家");
        album.setText("未知专辑");*/
        HBox musicHBox = new HBox();
        musicHBox.getStylesheets().add(getClass().getResource("resources/musicHBox.css").toExternalForm());
        musicHBox.getStyleClass().add("music-hbox");
        musicHBox.getChildren().addAll(musicName,artist,timeSpan/*,album*/);
        //musicHBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1199ee"),new CornerRadii(0), Insets.EMPTY)));
        System.out.println("歌曲名:"+ musicName.getText() + "  专辑名:" + album.getText() + "  艺术家:" + artist.getText());
        return musicHBox;
    }
    /*public void setMusicName(){

    }*/
}
