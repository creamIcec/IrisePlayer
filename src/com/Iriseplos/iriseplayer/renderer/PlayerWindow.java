package com.Iriseplos.iriseplayer.renderer;

import com.Iriseplos.iriseplayer.agent.Agent;
import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.PlayingStatus;
import com.Iriseplos.iriseplayer.renderer.musiclist.MusicListUI;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerWindow extends BaseWindow implements GeneralRender {
    //通用循环变量
    int index = 0;
    //根节点，包含所有控件
    VBox playerRoot = new VBox();
    //包括除了窗口控制(最小化等按钮)外的所有控件
    HBox playerOverAll = new HBox();
    //包含播放列表，添加按钮和标题的div
    VBox musicListView = new VBox();
    //正在播放的文件信息及播放控件
    VBox playerMain = new VBox();
    //正在播放的文件信息(艺术家，专辑，曲名)
    VBox infoMain = new VBox();
    //左列导航三个按钮
    VBox navigator = new VBox();
    //音乐播放控件
    HBox controlBar = new HBox();
    //调整控件(用于提供空间，类似于div)
    HBox leftMain = new HBox();
    //堆叠盒子，用于设计按钮的动画
    StackPane[] buttonPanes = {new StackPane(),new StackPane(),new StackPane(),new StackPane()};
    //标题
    Label musicTitle = new Label("未知标题");
    //艺术家
    Label artist = new Label("未知音乐家");
    //专辑
    Label album = new Label("未知专辑");
    //进度条旁显示时长
    Label timeSpan = new Label();
    //专辑封面
    ImageView musicIcon = new ImageView(new Image(getImage("resources/testImage/testImage.png")));
    //"播放列表"这个标题
    Label musicListTitle = new Label("播放列表");

    //播放列表的UI+标题
    MusicListUI MLUI = new MusicListUI();

    //TODO 美化播放列表
    //列表UI
    ListView<HBox> musicListViewUI = MLUI.generateListView();
    //播放进度控制条
    Slider playProgress = new Slider();
    //控制按钮对应图像
    Image[] musicControlIcons = {new Image(getImage("resources/icon/volume.png")), new Image(getImage("resources/icon/last.png")), new Image(getImage("resources/icon/play.png")), new Image(getImage("resources/icon/next.png")), new Image(getImage("resources/icon/pause.png"))};
    //控制按钮图像容器(用于显示)
    ImageView[] musicControls = {new ImageView(musicControlIcons[0]), new ImageView(musicControlIcons[1]), new ImageView(musicControlIcons[2]), new ImageView(musicControlIcons[3])};
    Button[] musicControlsButton = {new Button("",musicControls[0]),new Button("",musicControls[1]), new Button("",musicControls[2]),new Button("",musicControls[3])};
    //左侧三个导航按钮的图像
    Image[] navigatorIcons = {new Image(getImage("resources/icon/home.png")), new Image(getImage("resources/icon/settings.png")), new Image(getImage("resources/icon/plugins.png"))};
    //左侧三个导航按钮图像容器(用于显示)
    ImageView[] navigatorControls = {new ImageView(navigatorIcons[0]), new ImageView(navigatorIcons[1]), new ImageView(navigatorIcons[2])};
    //音量控制条
    Slider volumeControlBar = new Slider(-80.0,6.0206,6.0206);

    //渲染参数: 控制音量控制条在窗口中的位置
    Translate volumeBarPos = new Translate();

    //渲染参数: 控制音量控制条的尺寸
    Scale volumeBarSize = new Scale(0.5,1);
    //"添加"按钮
    HBoxButton addMusic = new HBoxButton("resources/icon/add.png", "添加");

    HBoxButton removeMusic = new HBoxButton("resources/icon/delete.png","删除");

    Rectangle[] buttonBackCovers = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    //TODO 添加文件夹功能
    //场景
    Scene scene = new Scene(playerRoot, 1140, 740);
    //舞台，相当于窗口
    Stage playerStage = new Stage();
    //代理人，用于前后端沟通
    Agent playAgent = Start.getAgent();
    //点击"添加"按钮后选择的音乐文件
    private File selectedFile;
    //音乐长度，用于确定播放进度条的位置
    private float musicLength;
    //标记是否为初次加载，便于第一次加载直接播放(具体见下面方法)
    private boolean isFirstLoaded = true;

    private Timeline buttonTransitionTimeline = new Timeline();

    private AnimationGenerator animationGenerator;
    //(弃用)标记是否播放结束已自动切换
    @Deprecated
    private boolean isMusicAutoChanged = false;
    //(弃用)标记是否点击了列表
    @Deprecated
    private boolean isMusicCellClicked = false;
    //(弃用)专辑图片的包装
    @Deprecated
    Rectangle albumIconWrap;
    //TODO: 按钮悬停鼠标动画

    //播放窗口构造方法
    public PlayerWindow() throws InvalidDataException, UnsupportedTagException, IOException {
        super();
        // 指定是否标准窗口（标准窗口有标题栏和边框)
        initStyle(StageStyle.UNDECORATED); // 默认为 DECORATED

        // 指定Scene
        setScene(scene);

        // 调整窗口适应Scene大小
        sizeToScene();

        // 启用拖拽支持
        initDragSupport(playerRoot, playerStage);
    }
    public Rectangle getMusicControlButtonCover(int index){
        return buttonBackCovers[index];
    }
    //初始化方法：渲染窗口，调用事件绑定方法，绑定动态渲染
    public void start() throws Exception {
        drawUI();
        scene.setFill(Paint.valueOf("#ffffff00"));
        playerStage.setTitle("Irise Player");
        playerStage.initStyle(StageStyle.TRANSPARENT);
        playerStage.setScene(scene);
        playerStage.show();
        bindEvents();
        onOpenFile();
        new RefreshProgressThread().start();
    }

    //设置按钮渐变动画
    /*private void setButtonTransitionTimelineEffect(){
        ButtonTransitionTimeline.setCycleCount(0);//设置周期运行循环往复
        ButtonTransitionTimeline.setAutoReverse(false);//设置动画的自动往返效果
        for(Button mcbutton : musicControlsButton) {
            final KeyValue kv = new KeyValue(mcbutton.getBackground().get, 0);
            final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        }
        //将关键帧加到时间轴中
        timeline.getKeyFrames().add(kf);

    }*/

    //绘制窗口
    @Override
    public void drawUI() {
        getStyleSheet(scene, "resources/player.css");
        playerOverAll.setId("hbox-overall");
        //System.out.println(scene.getHeight());
        playerOverAll.setPrefHeight(scene.getHeight());
        playerOverAll.setSpacing(scene.getWidth() * 0.1);
        playerMain.setSpacing(scene.getHeight() * 0.1);
        for (ImageView nIs : navigatorControls) {
            nIs.setStyle("-fx-scale-x:0.5;-fx-scale-y:0.5");
        }
        toolBar.setId("tool-bar");
        playerMain.setId("main");
        playerRoot.setId("vbox-root");
        musicListView.setId("music-list");
        drawWindowOperatorButtons();
        for (ImageView mcis : musicControls) {
            mcis.setStyle("-fx-scale-x:0.5;-fx-scale-y:0.5");
        }
        for(index=0;index<musicControlsButton.length;index++){
            musicControlsButton[index].setStyle("-fx-background-color:rgba(0,0,0,0.0)");
            musicControlsButton[index].setPrefWidth(70.0);
            musicControlsButton[index].setPrefHeight(70.0);
            musicControlsButton[index].setId(String.valueOf(index));
        }
        musicTitle.getStyleClass().add("title");
        musicListTitle.getStyleClass().add("title");
        artist.setStyle("-fx-font-size:15px;-fx-font-weight:200");
        album.setStyle("-fx-font-size:15px;-fx-font-weight:200");
        musicIcon.setFitWidth(200);
        musicIcon.setFitHeight(200);
        for(index =0;index<=3;index++) {
            buttonBackCovers[index].setFill(Paint.valueOf("rgba(0,0,0,0.0)"));
            buttonBackCovers[index].setWidth(musicControlsButton[0].getScaleX() * musicControlsButton[0].getPrefWidth());
            buttonBackCovers[index].setHeight(musicControlsButton[0].getScaleY() * musicControlsButton[0].getPrefHeight());
            buttonBackCovers[index].setArcWidth(10);
            buttonBackCovers[index].setArcHeight(10);
        }
        musicListViewUI.setId("music-list-content");
        /*volumeBarPos.setY(-50);
        volumeControlBar.getTransforms().add(volumeBarPos);*/
        playerRoot.getChildren().addAll(toolBar, playerOverAll);
        playerOverAll.getChildren().addAll(leftMain, musicListView);
        leftMain.getChildren().addAll(navigator, playerMain);
        musicListView.getChildren().addAll(musicListTitle, addMusic, removeMusic, musicListViewUI);
        navigator.getChildren().addAll(navigatorControls[0], navigatorControls[1], navigatorControls[2]);
        playerMain.getChildren().addAll(infoMain, musicIcon, playProgress, controlBar,volumeControlBar);
        infoMain.getChildren().addAll(musicTitle, artist, album);
        for(index = 0;index<=3;index++) {
            buttonPanes[index].getChildren().addAll(buttonBackCovers[index], musicControlsButton[index]);
        }
        controlBar.getChildren().addAll(buttonPanes);
        //动态渲染
        playProgress.setValue(0.0d);
        playProgress.setPrefWidth(scene.getWidth()/2);
        volumeControlBar.setPrefWidth(scene.getWidth()/2);
        volumeBarPos.setX(-250);
        volumeBarPos.setY(-80);
        volumeBarSize.setPivotX(volumeControlBar.getPrefWidth()/2);
        volumeBarSize.setPivotY(volumeControlBar.getPrefHeight());
        volumeControlBar.getTransforms().addAll(volumeBarSize,volumeBarPos);
        volumeControlBar.setVisible(false);
        System.out.println("已创建播放器窗口!");
        System.out.println("进度条长度:"+playProgress.getMax());
    }
    //绑定事件监听
    @Override
    public void bindEvents() {
        super.bindEvents();
        animationGenerator = new AnimationGenerator();
        musicControlsButton[2].setOnMouseClicked(new PlayMusic());
        musicControlsButton[0].setOnMouseClicked(new PauseMusic());
        closeWindowButton.setOnMouseClicked(new MouseClickClose());
        addMusic.setOnMouseClicked(new MouseClickAdd());
        musicListViewUI.setOnMouseClicked(new MouseClickMusicItemCell());
        musicControlsButton[3].setOnMouseClicked(new MouseClickNext());
        musicControlsButton[1].setOnMouseClicked(new MouseClickLast());
        musicControlsButton[0].setOnMouseClicked(new MouseClickVolume());
        volumeControlBar.setOnMouseReleased(new controlVolume());
        playProgress.setOnMouseReleased(new controlPosition());
        removeMusic.setOnMouseClicked(new mouseClickRemove());
        for (index=0;index<musicControlsButton.length;index++) {
            musicControlsButton[index].setOnMouseEntered(mouseEvent -> {
                //mcis.setStyle("-fx-background-color:rgba(200,211,217,0.7)");
                Button btn = (Button)mouseEvent.getSource();
                buttonTransitionTimeline = animationGenerator.getTimeLine(AnimationGenerator.AnimationType.IN, Integer.parseInt(btn.getId()));
                buttonTransitionTimeline.stop();
                buttonTransitionTimeline.play();
            });
            musicControlsButton[index].setOnMouseExited(mouseEvent -> {
                //mcis.setStyle("-fx-background-color:rgba(0,0,0,0.0)");
                Button btn = (Button)mouseEvent.getSource();
                buttonTransitionTimeline = animationGenerator.getTimeLine(AnimationGenerator.AnimationType.OUT, Integer.parseInt(btn.getId()));
                buttonTransitionTimeline.stop();
                buttonTransitionTimeline.play();
            });
        }
    }
    protected void drawWindowOperatorButtons() {
        closeWindowButton.getStyleClass().add("window-operator-buttons");
        maximizeWindowButton.getStyleClass().add("window-operator-buttons");
        minimizeWindowButton.getStyleClass().add("window-operator-buttons");
        ImageView[] windowOperatorIcons = {closeWindowButtonIcon, minimizeWindowButtonIcon, maximizeWindowButtonIcon};
        for (ImageView toChangeO : windowOperatorIcons) {
            toChangeO.setStyle("-fx-scale-x:0.25;-fx-scale-y:0.25");
        }
    }

    //用于打开文件。点击按钮时，向Agent传一个文件路径，agent帮忙写入列表中
    private void onOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择音频文件");
        List extList = new ArrayList();
        extList.add("*.wav");
        extList.add("*.mp3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("支持的音频格式文件",extList));
        selectedFile = fileChooser.showOpenDialog(playerStage);
        if (selectedFile == null) {
            return;
        }
        try {
            if(isFirstLoaded) {
                playAgent.agentAddMusic(selectedFile);
                playAgent.agentSetMusic(0);
                musicLength = playAgent.agentGetTotalLength(selectedFile);
                musicIcon.setImage(playAgent.agentGetAlbumIcon());
                musicTitle.setText(playAgent.agentGetCurrentMusicName());
                artist.setText(playAgent.agentGetCurrentMusicArtist());
                album.setText(playAgent.agentGetCurrentMusicAlbum());
                isFirstLoaded = false;
            }else{
                playAgent.agentAddMusic(selectedFile);
            }
            MLUI.addListViewContent(selectedFile);
            //System.out.println("获取的内容: "+musicListViewUI.getItems().get(0).getChildren());
           /* albumIconWrap = new Rectangle(musicIcon.getFitHeight(),musicIcon.getFitWidth());
            System.out.println("矩形的X坐标:"+albumIconWrap.getLayoutX()+"矩形的Y坐标:"+albumIconWrap.getLayoutY());
            albumIconWrap.setLayoutX(musicIcon.getLayoutX());
            albumIconWrap.setLayoutY(musicIcon.getLayoutY());
            System.out.println("图标的X坐标:"+musicIcon.getLayoutX()+"图标的Y坐标"+musicIcon.getLayoutY());
            System.out.println("矩形的X坐标:"+albumIconWrap.getLayoutX()+"矩形的Y坐标:"+albumIconWrap.getLayoutY());
            albumIconWrap.setArcWidth(20);
            albumIconWrap.setHeight(20);
            musicIcon.setClip(albumIconWrap);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //(弃用)获取当前代理人
    @Deprecated
    public Agent getPlayAgent(){
        return playAgent;
    }

    public void autoChange(){
        Platform.runLater(() -> {
            try {
                musicLength = playAgent.agentGetTotalLength(playAgent.agentGetCurrentPlayingFile());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                musicIcon.setImage(playAgent.agentGetAlbumIcon());
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
            musicTitle.setText(playAgent.agentGetCurrentMusicName());
            artist.setText(playAgent.agentGetCurrentMusicArtist());
            album.setText(playAgent.agentGetCurrentMusicAlbum());
        });
    }

    private void resetControls(){
        try {
            PlayingStatus ps = playAgent.agentCheckPlayingStatus();
            if (ps == PlayingStatus.IS_PLAYING) {
                musicControls[2].setImage(musicControlIcons[2]);
            } else if (ps == PlayingStatus.PAUSED) {
                musicControls[2].setImage(musicControlIcons[4]);
            } else if (ps == PlayingStatus.INITIAL_LOAD || ps == PlayingStatus.STOPPED) {
                musicControls[2].setImage(musicControlIcons[4]);
            }
            playAgent.controlStatus();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class RefreshProgressThread extends Thread {
        double currentProgress;

        //float audioTotalLength = playAgent.agentGetTotalLength(selectedFile);

        private RefreshProgressThread() {
        }

        @Override
        public void run() {
            try {
                while (playerStage.isShowing()) {
                    currentProgress = playProgress.getMax()*playAgent.agentGetPlayedLength() / musicLength;
                    Platform.runLater(() -> playProgress.setValue(currentProgress));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class MouseClickMusicItemCell implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){
                //System.out.println("切换歌曲!");
                try {
                    int index = musicListViewUI.getSelectionModel().getSelectedIndex();
                    if(index != -1) {
                        selectedFile = playAgent.agentSetMusic(index);
                        musicLength = playAgent.agentGetTotalLength(selectedFile);
                        musicIcon.setImage(playAgent.agentGetAlbumIcon());
                        musicTitle.setText(playAgent.agentGetCurrentMusicName());
                        artist.setText(playAgent.agentGetCurrentMusicArtist());
                        album.setText(playAgent.agentGetCurrentMusicAlbum());
                        resetControls();
                        musicListViewUI.getSelectionModel().clearSelection();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class PlayMusic implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            /*if(!isPlaying) {
                if(isInitiallyLoaded){
                    try {
                        System.out.println("已开始播放!");
                        isPlaying = true;
                        isInitiallyLoaded = false;
                        musicPlayer.start(true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                        isPlaying = true;
                    try {
                        musicPlayer.continueMusic();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                System.out.println("暂停!");
                isPlaying = false;
                musicPlayer.pause();
            }*/
            try {
                PlayingStatus ps = playAgent.agentCheckPlayingStatus();
                if (ps == PlayingStatus.IS_PLAYING) {
                    musicControls[2].setImage(musicControlIcons[2]);
                } else if (ps == PlayingStatus.PAUSED) {
                    musicControls[2].setImage(musicControlIcons[4]);
                } else if (ps == PlayingStatus.INITIAL_LOAD || ps == PlayingStatus.STOPPED) {
                    musicControls[2].setImage(musicControlIcons[4]);
                }
                playAgent.controlStatus();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private class PauseMusic implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            playAgent.toPause();
        }
    }

    protected class MouseClickClose implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            playerStage.close();
            try {
                if(playAgent.agentCheckPlayingStatus() == PlayingStatus.IS_PLAYING || playAgent.agentCheckPlayingStatus() == PlayingStatus.PAUSED) {
                    playAgent.toStop();
                }
                Platform.exit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class MouseClickNext implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                playAgent.agentNext();
                musicIcon.setImage(playAgent.agentGetAlbumIcon());
                musicTitle.setText(playAgent.agentGetCurrentMusicName());
                artist.setText(playAgent.agentGetCurrentMusicArtist());
                album.setText(playAgent.agentGetCurrentMusicAlbum());
                resetControls();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class MouseClickLast implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                playAgent.agentLast();
                musicIcon.setImage(playAgent.agentGetAlbumIcon());
                musicTitle.setText(playAgent.agentGetCurrentMusicName());
                artist.setText(playAgent.agentGetCurrentMusicArtist());
                album.setText(playAgent.agentGetCurrentMusicAlbum());
                resetControls();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class MouseClickAdd implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            onOpenFile();
        }
    }

    protected class MouseClickVolume implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            volumeControlBar.setVisible(!volumeControlBar.isVisible());
        }
    }

    protected class controlVolume implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            playAgent.agentControlVolume((float) volumeControlBar.getValue());
        }
    }

    //TODO: 实现从列表中移除音乐
    private class mouseClickRemove implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
                try {
                    int index = musicListViewUI.getSelectionModel().getSelectedIndex();
                    if(index != -1) {
                        String res = playAgent.agentRemoveMusic(index);
                        MLUI.removeListViewContent(index);
                        System.out.println("已删除:"+res);
                        musicListViewUI.getSelectionModel().clearSelection();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
    }

    protected class controlPosition implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                playAgent.agentChangePosition(playProgress.getValue()/100.0d);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    /*private Paint getEffectforRoot(){
        return new BoxBlur();
    }*/

}
