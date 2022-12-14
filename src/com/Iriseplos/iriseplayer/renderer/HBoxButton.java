package com.Iriseplos.iriseplayer.renderer;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class HBoxButton extends HBox {
    ImageView icon;
    Label description = new Label();
    protected void getStyleSheet(){
        try{
            this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/hboxButton.css")).toExternalForm());
        }catch(NullPointerException exception){
            exception.printStackTrace();
        }
    }
    protected ImageView getImageForIcon(String URL){
        return new ImageView(new Image(getImage(URL)));
    }
    public HBoxButton(String iconForImageURL, String textForLabel, int width){
        super();
        getStyleSheet();
        icon = this.getImageForIcon(iconForImageURL);
        this.getStyleClass().addAll("hbox-style","hbox-button-load");
        description.setText(textForLabel);
        icon.getStyleClass().add("icon-load");
        this.setSpacing(10);
        this.setMaxWidth(width);
        this.setPrefWidth(width);
        this.getChildren().addAll(icon,description);
    }
    public HBoxButton(String iconForImageURL,String textForLabel){
        this(iconForImageURL,textForLabel,400);
    }
    public String getImage(String URL){
        try {
            return Objects.requireNonNull(getClass().getResource(URL)).toExternalForm();
        }catch(NullPointerException exception){
            exception.printStackTrace();
            return null;
        }
    }
}
