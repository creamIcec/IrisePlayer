package com.Iriseplos.iriseplayer.renderer;

import com.Iriseplos.iriseplayer.agent.Agent;
import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Start extends Application {
    private static final Agent playAgent;

    static {
        try {
            playAgent = new Agent();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private static StartUpWindow startUpWindow;
    @Override
    public void start(Stage stage) throws InvalidDataException, UnsupportedTagException, IOException {
        startUpWindow = new StartUpWindow();
        startUpWindow.start();
    }
    public static Agent getAgent(){
        return playAgent;
    }
    public static StartUpWindow getStartUpWindow(){
        return startUpWindow;
    }
    public static void run(){
        launch();
    }
}
