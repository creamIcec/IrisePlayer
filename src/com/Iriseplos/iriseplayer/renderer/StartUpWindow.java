package com.Iriseplos.iriseplayer.renderer;


import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class StartUpWindow extends BaseWindow implements GeneralRender{
    VBox initRoot = new VBox();
    HBox initPaneOverAll = new HBox();

    HBoxButton openLastPlayedListButton = new HBoxButton("resources/icon/list.png","打开上次播放的列表",300);
    HBoxButton openFileButton = new HBoxButton("resources/icon/openfile.png","打开文件",300);
    HBoxButton openFolderButton = new HBoxButton("resources/icon/open-folder.png","打开文件夹",300);
    VBox initPaneLeft = new VBox();

    Scene scene = new Scene(initRoot, 1140, 740);
    Stage startupStage = new Stage();
    Circle mouseCircle = new Circle();
    Translate circlePos = new Translate();

    Group forCircleBlurGroup = new Group();

    PlayerWindow pw;

    public StartUpWindow(){
        super();

        // 指定是否标准窗口（标准窗口有标题栏和边框)
        initStyle(StageStyle.UNDECORATED); // 默认为 DECORATED

        // 指定Scene
        setScene(scene);

        // 调整窗口适应Scene大小
        sizeToScene();
        initDragSupport(initRoot,startupStage); // 拖拽支持
        //startupStage.setResizable(true);
    }
    public void start(){
        drawUI();
        scene.setFill(Paint.valueOf("#ffffff00"));
        startupStage.setTitle("Irise Player");
        startupStage.initStyle(StageStyle.TRANSPARENT);
        startupStage.setScene(scene);
        startupStage.show();
        bindEvents();
    }
    @Override
    public void drawUI(){
        Label initTitle = new Label("起始页");
        Label recentPlayed = new Label("最近播放");
        Label versionDescription = new Label(" IrisePlayer beta-v1.0\n 本程序用户界面受Violet Player启发。\n 正式版测试阶段 \n Made by Iriseplos and all he loved.");
        Image[] iconFiles = {new Image(getImage("resources/icon/openfile.png")),new Image(getImage("resources/icon/open-folder.png"))};
        ImageView[] iconViews = {new ImageView(iconFiles[0]),new ImageView(iconFiles[1])};
        getStyleSheet(scene,"resources/startUp.css");
        //窗口控制按钮处理
        drawWindowOperatorButtons();
        //编组处理
        forCircleBlurGroup.setBlendMode(BlendMode.COLOR_BURN);
        forCircleBlurGroup.getChildren().addAll(mouseCircle,openFileButton);
        //鼠标圆处理
        mouseCircle.setCenterX(0);
        mouseCircle.setCenterY(0);
        mouseCircle.setRadius(30);
        mouseCircle.setFill(Paint.valueOf("#00000020"));
        mouseCircle.setVisible(false);
        //效果处理
        mouseCircle.setEffect(getMouseCircleEffect());
        mouseCircle.setBlendMode(BlendMode.COLOR_BURN);
        //openFileButton.setBlendMode(BlendMode.COLOR_DODGE);
        //添加css的id和class
        iconViews[1].setStyle("-fx-scale-x:0.5;-fx-scale-y:0.5");
        versionDescription.getStyleClass().add("version-description");
        initPaneOverAll.getStyleClass().addAll("hbox-style");
        initPaneOverAll.setId("hbox-overall");
        initTitle.getStyleClass().add("title");
        recentPlayed.getStyleClass().add("title");
        initPaneLeft.setId("vbox-left");
        initRoot.setId("vbox-root");
        toolBar.setId("tool-bar");
        initPaneLeft.getChildren().addAll(initTitle,openFileButton,openFolderButton,recentPlayed,openLastPlayedListButton);
        initPaneOverAll.getChildren().addAll(initPaneLeft,versionDescription);
        initRoot.getChildren().addAll(toolBar,mouseCircle,initPaneOverAll);
    }
    @Override
    public void bindEvents(){
        super.bindEvents();
        this.openFileButton.setOnMouseEntered(new MouseEnterButton());
        this.openFileButton.setOnMouseMoved(new MouseOverButton());
        this.openFileButton.setOnMouseExited(new MouseOutButton());
        this.openFileButton.setOnMouseClicked(new MouseClickOpenFile());
        this.openFolderButton.setOnMouseClicked(new MouseClickOpenFolder());
        this.openLastPlayedListButton.setOnMouseClicked(new MouseClickOpenLast());
    }
    private Effect getMouseCircleEffect(){
        BoxBlur bb = new BoxBlur();
        bb.setWidth(20);
        bb.setHeight(20);
        return bb;
    }
    private class MouseOverButton implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent e) {
            double resX = -(mouseCircle.getLayoutX() - e.getSceneX());
            double resY = -(mouseCircle.getLayoutY() - e.getSceneY());
            circlePos.setX(resX);
            circlePos.setY(resY);
            //System.out.println(mouseCircle.getLayoutY()+" "+e.getSceneY()+" "+resY);
            mouseCircle.getTransforms().clear();
            mouseCircle.getTransforms().addAll(circlePos);
        }
    }
    private class MouseOutButton implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseCircle.setVisible(false);
        }
    }
    private class MouseEnterButton implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseCircle.setVisible(true);
        }
    }
    private class MouseClickOpenFile implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent){
            try {
                pw = new PlayerWindow();
                pw.start(PlayerWindow.openType.FILE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            startupStage.close();
        }
    }
    private class MouseClickOpenFolder implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent){
            try {
                pw = new PlayerWindow();
                pw.start(PlayerWindow.openType.FOLDER);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            startupStage.close();
        }
    }
    private class MouseClickOpenLast implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                pw = new PlayerWindow();
                pw.start(PlayerWindow.openType.LASTPLAYED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            startupStage.close();
        }
    }
    @Override
    protected void drawWindowOperatorButtons(){
        closeWindowButton.getStyleClass().add("window-operator-buttons");
        maximizeWindowButton.getStyleClass().add("window-operator-buttons");
        minimizeWindowButton.getStyleClass().add("window-operator-buttons");
        ImageView[] windowOperatorIcons = {closeWindowButtonIcon,minimizeWindowButtonIcon,maximizeWindowButtonIcon};
        for(ImageView toChangeO : windowOperatorIcons){
            toChangeO.setStyle("-fx-scale-x:0.25;-fx-scale-y:0.25");
        }
    }

    public PlayerWindow getPlayerWindow(){
        return pw;
    }
}
