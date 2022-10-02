package com.Iriseplos.iriseplayer.renderer.musiclist;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.filesystem.MusicList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
        musicName.getStyleClass().add("music-info");
        artist.getStyleClass().add("music-info");
        album.getStyleClass().add("music-info");
        timeSpan.getStyleClass().add("music-info");
        musicName.setPrefWidth(150);
        artist.setPrefWidth(150);
        album.setPrefWidth(150);
        HBox musicHBox = new HBox();
        musicHBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/musicHBox.css")).toExternalForm());
        musicHBox.getStyleClass().add("music-hbox");
        musicHBox.getChildren().addAll(musicName,/*artist,*/timeSpan/*,album*/);
        //musicHBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1199ee"),new CornerRadii(0), Insets.EMPTY)));
        System.out.println("歌曲名:"+ musicName.getText() + "  专辑名:" + album.getText() + "  艺术家:" + artist.getText());
        return musicHBox;
    }
}
