package com.Iriseplos.iriseplayer.renderer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class AnimationGenerator {
    PlayerWindow targetWindow = Start.getStartUpWindow().getPlayerWindow();
    enum AnimationType{IN,OUT}

    private final String fullColor = "rgba(243,204,255,1.0)";
    private final String emptyColor = "rgba(0,0,0,0.0)";
    public KeyFrame fullColored1 = new KeyFrame(Duration.seconds(0.3),"fullColor",new KeyValue(targetWindow.getMusicControlButtonCover(0).fillProperty(), Paint.valueOf(fullColor), Interpolator.EASE_BOTH));
    public KeyFrame fullColored2 = new KeyFrame(Duration.seconds(0.3),"fullColor",new KeyValue(targetWindow.getMusicControlButtonCover(1).fillProperty(), Paint.valueOf(fullColor), Interpolator.EASE_BOTH));
    public KeyFrame fullColored3 = new KeyFrame(Duration.seconds(0.3),"fullColor",new KeyValue(targetWindow.getMusicControlButtonCover(2).fillProperty(), Paint.valueOf(fullColor), Interpolator.EASE_BOTH));
    public KeyFrame fullColored4 = new KeyFrame(Duration.seconds(0.3),"fullColor",new KeyValue(targetWindow.getMusicControlButtonCover(3).fillProperty(), Paint.valueOf(fullColor), Interpolator.EASE_BOTH));

    public KeyFrame fullColored5 = new KeyFrame(Duration.seconds(0.3),"fullColor",new KeyValue(targetWindow.getMusicControlButtonCover(4).fillProperty(), Paint.valueOf(fullColor), Interpolator.EASE_BOTH));
    public KeyFrame emptyColored1 = new KeyFrame(Duration.seconds(0.3),"emptyColor",new KeyValue(targetWindow.getMusicControlButtonCover(0).fillProperty(),Paint.valueOf(emptyColor),Interpolator.EASE_BOTH));
    public KeyFrame emptyColored2 = new KeyFrame(Duration.seconds(0.3),"emptyColor",new KeyValue(targetWindow.getMusicControlButtonCover(1).fillProperty(),Paint.valueOf(emptyColor),Interpolator.EASE_BOTH));
    public KeyFrame emptyColored3 = new KeyFrame(Duration.seconds(0.3),"emptyColor",new KeyValue(targetWindow.getMusicControlButtonCover(2).fillProperty(),Paint.valueOf(emptyColor),Interpolator.EASE_BOTH));
    public KeyFrame emptyColored4 = new KeyFrame(Duration.seconds(0.3),"emptyColor",new KeyValue(targetWindow.getMusicControlButtonCover(3).fillProperty(),Paint.valueOf(emptyColor),Interpolator.EASE_BOTH));
    public KeyFrame emptyColored5 = new KeyFrame(Duration.seconds(0.3),"emptyColor",new KeyValue(targetWindow.getMusicControlButtonCover(4).fillProperty(),Paint.valueOf(emptyColor),Interpolator.EASE_BOTH));
    public Timeline[] buttonHoverInTimeLines = {new Timeline(),new Timeline(),new Timeline(),new Timeline(),new Timeline()};
    public Timeline[] buttonHoverOutTimeLines = {new Timeline(),new Timeline(),new Timeline(),new Timeline(),new Timeline()};
    private void initializeTimeLine(){
        buttonHoverInTimeLines[0].getKeyFrames().add(fullColored1);
        buttonHoverInTimeLines[1].getKeyFrames().add(fullColored2);
        buttonHoverInTimeLines[2].getKeyFrames().add(fullColored3);
        buttonHoverInTimeLines[3].getKeyFrames().add(fullColored4);
        buttonHoverInTimeLines[4].getKeyFrames().add(fullColored5);
        buttonHoverOutTimeLines[0].getKeyFrames().add(emptyColored1);
        buttonHoverOutTimeLines[1].getKeyFrames().add(emptyColored2);
        buttonHoverOutTimeLines[2].getKeyFrames().add(emptyColored3);
        buttonHoverOutTimeLines[3].getKeyFrames().add(emptyColored4);
        buttonHoverOutTimeLines[4].getKeyFrames().add(emptyColored5);
    }
    public Timeline getTimeLine(AnimationType type, int index){
        switch (type){
            case IN -> {
                return buttonHoverInTimeLines[index];
            }
            case OUT -> {
                return buttonHoverOutTimeLines[index];
            }
            default -> {
                System.out.println("类型不在列表中。应使用:IN或OUT");
                return null;
            }
        }
    }
    public AnimationGenerator(){
        initializeTimeLine();
    }
}
