package com.Iriseplos.iriseplayer.renderer;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class BaseWindow extends Stage{
    private Stage stage;
    private boolean dragging = false;
    private double windowX = 0, windowY = 0; // 初始窗口坐标
    private double windowW = 0, windowH = 0; // 初始窗口大小
    private double startX, startY;  // 鼠标按下位置的相对坐标
    private double startScreenX, startScreenY;  // 鼠标相对屏幕Screen坐标
    protected HBox toolBar = new HBox();
    Button closeWindowButton;
    Button maximizeWindowButton;
    Button minimizeWindowButton;

    Image maximizeButtonImage = new Image(getImage("resources/icon/maximize.png"));
    Image miniwindowButtonImage = new Image(getImage("resources/icon/miniwindow.png"));
    protected ImageView closeWindowButtonIcon = new ImageView(new Image(getImage("resources/icon/close.png")));
    protected ImageView minimizeWindowButtonIcon = new ImageView(new Image(getImage("resources/icon/minimize.png")));
    protected ImageView maximizeWindowButtonIcon = new ImageView();

    protected boolean isMaximized;
    public BaseWindow(){
        isMaximized = false;
        maximizeWindowButtonIcon.setImage(maximizeButtonImage);
        closeWindowButton = new Button("",closeWindowButtonIcon);
        maximizeWindowButton = new Button("",maximizeWindowButtonIcon);
        minimizeWindowButton = new Button("",minimizeWindowButtonIcon);
        //toolBar.setStyle("-fx-border-width:0px 0px 2px 0px;-fx-border-color:#000000");   //设置工具栏范围
        toolBar.getChildren().addAll(minimizeWindowButton,maximizeWindowButton,closeWindowButton);
        //bindEvents();
    }
    protected void initDragSupport(Pane root,Stage stage)
    {
        this.stage = stage;

        root.setOnMousePressed((MouseEvent e)->{

            // 记录鼠标点下时，窗口的坐标、大小
            windowX = stage.getX();
            windowY = stage.getY();
            windowW = stage.getWidth();
            windowH = stage.getHeight();

            // 记录鼠标点下时，鼠标的位置
            startX = e.getX();
            startY = e.getY();
            startScreenX = e.getScreenX();
            startScreenY = e.getScreenY();
        });

        root.setOnDragDetected((MouseEvent e)->{
            if(e.getButton() == MouseButton.PRIMARY)
            {
                dragging = true;
            }
        });

        root.setOnMouseDragged((MouseEvent e)->{
            if(!dragging) return;

            // 计算鼠标的位移 , 起始点A点，当前位置 B点
            double dx = e.getScreenX() - startScreenX;
            double dy = e.getScreenY() - startScreenY;
            stage.setX ( windowX + dx);
            stage.setY ( windowY + dy);
        });

        root.setOnMouseReleased((MouseEvent e)-> dragging = false);
    }

    public String getImage(String URL){
        try {
            return Objects.requireNonNull(getClass().getResource(URL)).toExternalForm();
        }catch(NullPointerException exception){
            exception.printStackTrace();
            return null;
        }
    }
    protected void drawWindowOperatorButtons(){}
    protected void getStyleSheet(Scene scene, String URL){
        try{
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(URL)).toExternalForm());
        }catch(NullPointerException exception){
            exception.printStackTrace();
        }
    }
    protected void bindEvents() {
        minimizeWindowButton.setOnMouseClicked(new MouseClickMinimize());
        maximizeWindowButton.setOnMouseClicked(new MouseClickMaximize());
        closeWindowButton.setOnMouseClicked(new MouseClickClose());
    }
    protected class MouseClickClose implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            stage.close();
            Platform.exit();
        }
    }
    protected class MouseClickMinimize implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            stage.setIconified(true);
        }
    }
    protected class MouseClickMaximize implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            if(!isMaximized) {
                stage.setMaximized(true);
                maximizeWindowButtonIcon.setImage(miniwindowButtonImage);
                isMaximized = true;
            }else{
                stage.setMaximized(false);
                maximizeWindowButtonIcon.setImage(maximizeButtonImage);
                isMaximized = false;
            }
        }
    }
}
