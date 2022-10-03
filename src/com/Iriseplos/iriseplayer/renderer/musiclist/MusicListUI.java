package com.Iriseplos.iriseplayer.renderer.musiclist;

import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;


public class MusicListUI {
    //创建一个ListView(必须指定数据项类型)(指定一种箱子只能用来装某种东西)
    ListView<HBox> musicListView = new ListView<>();
    //数据源(这个箱子里的东西从哪儿来?)
    ObservableList<HBox> musicData = FXCollections.observableArrayList();

    private boolean isFirstLoad = true;

    //以下方法用于生成播放列表} catch (Exception e) {
    //                throw new RuntimeException(e);
    //            }
    public ListView<HBox> generateListView() throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        musicData.add(new MusicListHBox().getNewMusicHBox(null));
        //将数据源放进箱子里
        musicListView.setItems(musicData);
        //System.out.println("内容: "+musicListView.getItems().get(0).getChildren());
        //设置单元格生成器，用于当数据源项目改变时自动更新呈现列表
       /* musicListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<HBox> call(ListView<HBox> hBoxListView) {
                return new MusicListCell();
            }
        });*/
        return musicListView;
    }

    public void addListViewContent(File sourceFile) throws InvalidDataException, UnsupportedTagException, IOException, UnsupportedAudioFileException {
        if(isFirstLoad){
            musicData.remove(0);
            musicData.add(new MusicListHBox().getNewMusicHBox(sourceFile));
            isFirstLoad = false;
        }else {
            musicData.add(new MusicListHBox().getNewMusicHBox(sourceFile));
        }
    }

    public void removeListViewContent(int index){
        musicData.remove(index);
    }
}
