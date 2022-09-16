package com.Iriseplos.iriseplayer.renderer.musiclist;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class MusicListCell extends ListCell<HBox> {
    private boolean isInitialized = false;
    //操作单元格内容，当用户或程序改变ObserveList时时会调用
    @Override
    public void updateItem(HBox musicItem,boolean empty){
        super.updateItem(musicItem,empty);
        if (musicItem == null) {
            this.setText(""); // 清空显示
        } else {
            if (!isInitialized) {
                for (Node item : musicItem.getChildren()) {
                    this.setText(this.getText() + ((Label) item).getText() + "     "); // 显示该项的值
                }
                isInitialized = true;
            }
        }
    }
}
