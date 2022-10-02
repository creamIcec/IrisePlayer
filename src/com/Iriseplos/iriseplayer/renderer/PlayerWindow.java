package com.Iriseplos.iriseplayer.renderer;

import com.Iriseplos.iriseplayer.agent.Agent;
import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.MusicPlayer;
import com.Iriseplos.iriseplayer.player.PlayingStatus;
import com.Iriseplos.iriseplayer.renderer.musiclist.MusicListUI;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    StackPane[] buttonPanes = {new StackPane(), new StackPane(), new StackPane(), new StackPane(), new StackPane()};

    StackPane stackPaneRoot = new StackPane();

    StackPane progressContainer = new StackPane();

    VBox musicStatusContainer = new VBox();

    ImageView backgroundImage = new ImageView();
    //标题
    Label musicTitle = new Label("未知标题");
    //艺术家
    Label artist = new Label("未知音乐家");
    //专辑
    Label album = new Label("未知专辑");
    //进度条旁显示时长
    Label timeSpan = new Label();
    //专辑封面
    ImageView musicIcon = new ImageView();
    //"播放列表"这个标题
    Label musicListTitle = new Label("播放列表");

    //播放列表的UI+标题
    MusicListUI MusicListArea = new MusicListUI();

    //TODO 美化播放列表
    //列表UI
    ListView<HBox> musicListViewUI = MusicListArea.generateListView();
    //播放进度控制条
    Slider playProgress = new Slider();
    //播放顺序控制按钮对应图像
    Image[] playOrderControlIcons = {new Image(getImage("resources/icon/order.png")),new Image(getImage("resources/icon/loop.png")),new Image(getImage("resources/icon/random.png"))};
    //控制按钮对应图像
    Image[] musicControlIcons = {new Image(getImage("resources/icon/volume.png")), new Image(getImage("resources/icon/last.png")), new Image(getImage("resources/icon/play.png")), new Image(getImage("resources/icon/next.png")), new Image(getImage("resources/icon/pause.png"))};
    //控制按钮图像容器(用于显示)
    ImageView[] musicControls = {new ImageView(musicControlIcons[0]), new ImageView(musicControlIcons[1]), new ImageView(musicControlIcons[2]), new ImageView(musicControlIcons[3])};
    //播放顺序图像容器
    ImageView playOrderControl = new ImageView(playOrderControlIcons[0]);

    Button playOrderControlsButton = new Button("", playOrderControl);
    Button[] musicControlsButton = {new Button("", musicControls[0]), new Button("", musicControls[1]), new Button("", musicControls[2]), new Button("", musicControls[3]), playOrderControlsButton};
    //左侧三个导航按钮的图像
    Image[] navigatorIcons = {new Image(getImage("resources/icon/home.png")), new Image(getImage("resources/icon/settings.png")), new Image(getImage("resources/icon/plugins.png"))};
    //左侧三个导航按钮图像容器(用于显示)
    ImageView[] navigatorControls = {new ImageView(navigatorIcons[0]), new ImageView(navigatorIcons[1]), new ImageView(navigatorIcons[2])};
    //音量控制条
    Slider volumeControlBar = new Slider(-80.0, 6.0206, 6.0206);

    //渲染参数: 控制音量控制条在窗口中的位置
    Translate volumeBarPos = new Translate();

    //渲染参数: 控制音量控制条的尺寸
    Scale volumeBarSize = new Scale(0.5, 1);
    //"添加"两个按钮的容器
    HBox addWrapper = new HBox();
    //"添加"按钮
    HBoxButton addMusic = new HBoxButton("resources/icon/add.png", "添加", 200);
    //"添加文件夹"按钮
    HBoxButton addMusicFolder = new HBoxButton("resources/icon/open-folder.png", "添加文件夹", 200);

    HBoxButton removeMusic = new HBoxButton("resources/icon/delete.png", "从列表中移除");

    //播放控制按钮背景
    Rectangle[] buttonBackCovers = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(),new Rectangle()};
    //进度条Mask
    Rectangle sliderMask = new Rectangle();
    //记录当前的播放模式
    MusicPlayer.playOrderType currentOrderType;
    //场景宽度
    int sceneWidth = 1140;
    //场景长度
    int sceneHeight = 740;


    //TODO 添加文件夹功能
    //场景
    Scene scene = new Scene(stackPaneRoot, sceneWidth, sceneHeight);
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
    //按钮渐变动画时间轴
    private Timeline buttonTransitionTimeline = new Timeline();

    private final FadeTransition backgroundInTransition = new FadeTransition(Duration.seconds(0.8),backgroundImage);
    private AnimationGenerator animationGenerator;

    public enum openType {FILE, FOLDER, LASTPLAYED}

    //背景图片载体
    Rectangle backRect = new Rectangle(sceneWidth, sceneHeight);
    //(弃用)标记是否播放结束已自动切换
    //@Deprecated
    //private boolean isMusicAutoChanged = false;
    //(弃用)标记是否点击了列表
    //@Deprecated
    //private boolean isMusicCellClicked = false;
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
        initDragSupport(toolBar, playerStage);
    }

    public Rectangle getMusicControlButtonCover(int index) {
        return buttonBackCovers[index];
    }

    //初始化方法：渲染窗口，调用事件绑定方法，绑定动态渲染
    public void start(openType ot) throws Exception {
        drawUI();
        scene.setFill(Paint.valueOf("#ffffff00"));
        playerStage.setTitle("Irise Player");
        playerStage.initStyle(StageStyle.TRANSPARENT);
        playerStage.setScene(scene);
        playerStage.show();
        bindEvents();
        switch (ot) {
            case FILE -> onOpenFile();
            case FOLDER -> onOpenFolder();
            case LASTPLAYED -> onOpenLast();
        }
        new RefreshProgressThread().start();
        currentOrderType = MusicPlayer.playOrderType.ORDER;
    }

    private void genSliderMask() {
        sliderMask.heightProperty().bind(playProgress.heightProperty().subtract(7));
        sliderMask.widthProperty().bind(playProgress.widthProperty());
        sliderMask.setFill(Paint.valueOf("#111111"));
        sliderMask.setArcWidth(10);
        sliderMask.setArcHeight(10);
    }

    //绘制窗口
    @Override
    public void drawUI() {
        getStyleSheet(scene, "resources/player.css");
        playerOverAll.setId("hbox-overall");
        playerOverAll.setPrefHeight(scene.getHeight());
        playerOverAll.setSpacing(scene.getWidth() * 0.1);
        stackPaneRoot.setId("stackpane-root");
        playerMain.setSpacing(scene.getHeight() * 0.1);
        for (ImageView nIs : navigatorControls) {
            nIs.setStyle("-fx-scale-x:0.5;-fx-scale-y:0.5");
        }
        toolBar.setId("tool-bar");
        playerMain.setId("main");
        playerRoot.setId("vbox-root");
        musicListView.setId("music-list");
        navigator.setId("navigator-bar");
        musicListView.setPrefWidth(350);
        musicStatusContainer.setAlignment(Pos.CENTER_RIGHT);
        drawWindowOperatorButtons();
        for (ImageView mC : musicControls) {
            mC.setStyle("-fx-scale-x:0.5;-fx-scale-y:0.5");
        }
        for (index = 0; index < musicControlsButton.length; index++) {
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
        backgroundImage.setFitWidth(sceneWidth);
        backgroundImage.setFitHeight(sceneHeight);
        backgroundImage.setOpacity(0);
        for (index = 0; index < buttonBackCovers.length; index++) {
            buttonBackCovers[index].setFill(Paint.valueOf("rgba(0,0,0,0.0)"));
            buttonBackCovers[index].setWidth(musicControlsButton[0].getScaleX() * musicControlsButton[0].getPrefWidth());
            buttonBackCovers[index].setHeight(musicControlsButton[0].getScaleY() * musicControlsButton[0].getPrefHeight());
            buttonBackCovers[index].setArcWidth(10);
            buttonBackCovers[index].setArcHeight(10);
        }
        musicListViewUI.setId("music-list-content");
        backRect.setArcWidth(20);
        backRect.setArcHeight(20);
        stackPaneRoot.getChildren().addAll(backgroundImage, playerRoot);
        playerRoot.getChildren().addAll(toolBar, playerOverAll);
        playerOverAll.getChildren().addAll(leftMain, musicListView);
        leftMain.getChildren().addAll(navigator, playerMain);
        addWrapper.getChildren().addAll(addMusic, addMusicFolder);
        musicListView.getChildren().addAll(musicListTitle, addWrapper, removeMusic, musicListViewUI);
        navigator.getChildren().addAll(navigatorControls[0], navigatorControls[1], navigatorControls[2]);
        progressContainer.getChildren().addAll(sliderMask, playProgress);
        musicStatusContainer.getChildren().addAll(progressContainer,timeSpan);
        playerMain.getChildren().addAll(infoMain, musicIcon, musicStatusContainer,controlBar, volumeControlBar);
        infoMain.getChildren().addAll(musicTitle, artist, album);
        for (index = 0; index < buttonPanes.length; index++) {
            buttonPanes[index].getChildren().addAll(buttonBackCovers[index], musicControlsButton[index]);
        }
        controlBar.getChildren().addAll(buttonPanes);
        //动态渲染
        backgroundImage.setClip(backRect);
        backgroundImage.setEffect(new GaussianBlur(40));
        playProgress.setId("progress-bar");
        playProgress.setValue(0.0d);
        playProgress.setPrefWidth(scene.getWidth() / 2);
        volumeControlBar.setPrefWidth(scene.getWidth() / 2);
        volumeBarPos.setX(-250);
        volumeBarPos.setY(-80);
        volumeBarSize.setPivotX(volumeControlBar.getPrefWidth() / 2);
        volumeBarSize.setPivotY(volumeControlBar.getPrefHeight());
        volumeControlBar.getTransforms().addAll(volumeBarSize, volumeBarPos);
        volumeControlBar.setVisible(false);
        System.out.println("已创建播放器窗口!");
        System.out.println("进度条长度:" + playProgress.getMax());
    }

    //绑定事件监听
    @Override
    public void bindEvents() {
        super.bindEvents();
        for(index=0;index<musicControlsButton.length;index++){
            musicControlsButton[index].setId(String.valueOf(index));
        }
        animationGenerator = new AnimationGenerator();
        musicControlsButton[2].setOnMouseClicked(new PlayMusic());
        musicControlsButton[0].setOnMouseClicked(new PauseMusic());
        closeWindowButton.setOnMouseClicked(new MouseClickClose());
        addMusic.setOnMouseClicked(new MouseClickAdd());
        addMusicFolder.setOnMouseClicked(new MouseClickAddFolder());
        musicListViewUI.setOnMouseClicked(new MouseClickMusicItemCell());
        musicControlsButton[3].setOnMouseClicked(new MouseClickNext());
        musicControlsButton[1].setOnMouseClicked(new MouseClickLast());
        musicControlsButton[0].setOnMouseClicked(new MouseClickVolume());
        musicControlsButton[4].setOnMouseClicked(new MouseClickOrder());
        volumeControlBar.setOnMouseReleased(new ControlVolume());
        playProgress.setOnMouseReleased(new ControlPosition());
        removeMusic.setOnMouseClicked(new MouseClickRemove());
        for (index = 0; index < musicControlsButton.length; index++) {
            musicControlsButton[index].setOnMouseEntered(mouseEvent -> {
                Button btn = (Button) mouseEvent.getSource();
                buttonTransitionTimeline = animationGenerator.getTimeLine(AnimationGenerator.AnimationType.IN, Integer.parseInt(btn.getId()));
                buttonTransitionTimeline.stop();
                System.gc();
                buttonTransitionTimeline.play();
            });
            musicControlsButton[index].setOnMouseExited(mouseEvent -> {
                Button btn = (Button) mouseEvent.getSource();
                buttonTransitionTimeline = animationGenerator.getTimeLine(AnimationGenerator.AnimationType.OUT, Integer.parseInt(btn.getId()));
                buttonTransitionTimeline.stop();
                System.gc();
                buttonTransitionTimeline.play();
            });
        }
        genSliderMask();
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
        List<String> extList = new ArrayList<>();
        extList.add("*.wav");
        extList.add("*.mp3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("支持的音频格式文件", extList));
        selectedFile = fileChooser.showOpenDialog(playerStage);
        if (selectedFile == null) {
            return;
        }
        try {
            if (isFirstLoaded) {
                playAgent.agentAddMusic(selectedFile);
                playAgent.agentSetMusic(0);
                loadMusicInfoToScreen();
                playBackgroundInTransition();
                isFirstLoaded = false;
            } else {
                playAgent.agentAddMusic(selectedFile);
            }
            MusicListArea.addListViewContent(selectedFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //打开文件夹
    private void onOpenFolder() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File[] chosenFiles;
        directoryChooser.setTitle("选择音频文件所在文件夹");
        selectedFile = directoryChooser.showDialog(playerStage);
        if (selectedFile != null && selectedFile.isDirectory()) {
            chosenFiles = selectedFile.listFiles((dir, name) -> name.endsWith(".mp3"));
            if(chosenFiles != null && chosenFiles.length != 0) {
                for (File chosenFile : chosenFiles) {
                    playAgent.agentAddMusic(chosenFile);
                    MusicListArea.addListViewContent(chosenFile);
                }
            }else{
                Alert noMatchingFileAlert = new Alert(Alert.AlertType.WARNING,"选择的目录没有支持播放的文件。请重新选择。");
                Optional<ButtonType> result = noMatchingFileAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    onOpenFolder();
                }
            }
            if (isFirstLoaded) {
                playAgent.agentSetMusic(0);
                loadMusicInfoToScreen();
                playBackgroundInTransition();
                isFirstLoaded = false;
            }
        } else {
            onOpenFile();
        }
    }

    private void onOpenLast() throws Exception {
        File data = new File("userdata/lastList.txt");
        if (!data.exists()) {
            ButtonType optionOpenFile = new ButtonType("打开文件", ButtonBar.ButtonData.LEFT);
            ButtonType optionOpenFolder = new ButtonType("打开文件夹", ButtonBar.ButtonData.LEFT);
            Alert notExistAlert = new Alert(Alert.AlertType.WARNING, "没有找到上一次播放的数据呢?是不是第一次使用播放器或者删除了数据?",optionOpenFile,optionOpenFolder);
            Optional<ButtonType> result = notExistAlert.showAndWait();
            if(result.isPresent()) {
                if (result.get() == optionOpenFile) {
                    onOpenFile();
                    return;
                } else if (result.get() == optionOpenFolder){
                    onOpenFolder();
                    return;
                }else{
                    onOpenLast();
                }
            }else{
                onOpenLast();
            }
        }
        FileInputStream fis = new FileInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String str;
        while ((str = br.readLine()) != null) {
            File tmpFile = new File(str);
            playAgent.agentAddMusic(tmpFile);
            MusicListArea.addListViewContent(tmpFile);
        }
        if (isFirstLoaded) {
            playAgent.agentSetMusic(0);
            loadMusicInfoToScreen();
            playBackgroundInTransition();
            isFirstLoaded = false;
        }
        //close
        fis.close();
        br.close();
    }

    //(弃用)获取当前代理人
    @Deprecated
    public Agent getPlayAgent() {
        return playAgent;
    }

    public MusicPlayer.playOrderType getOrderType(){
        return currentOrderType;
    }

    private void loadMusicInfoToScreen() throws Exception {
        musicLength = playAgent.agentGetTotalLength(playAgent.agentGetCurrentPlayingFile());
        musicIcon.setImage(playAgent.agentGetAlbumIcon());
        musicTitle.setText(playAgent.agentGetCurrentMusicName());
        artist.setText(playAgent.agentGetCurrentMusicArtist());
        album.setText(playAgent.agentGetCurrentMusicAlbum());
        timeSpan.setText(playAgent.agentGetMusicLengthTime(playAgent.agentGetCurrentPlayingFile()));
        backgroundImage.setImage(playAgent.agentGetAlbumIcon());
        playBackgroundInTransition();
    }
    private void playBackgroundInTransition(){
        backgroundInTransition.setFromValue(0.0);
        backgroundInTransition.setToValue(1.0);
        backgroundInTransition.setAutoReverse(false);
        backgroundInTransition.play();
    }
    public void autoChange() {
        Platform.runLater(() -> {
            try {
                loadMusicInfoToScreen();
                playBackgroundInTransition();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void resetControls() {
        try {
            PlayingStatus ps = playAgent.agentCheckPlayingStatus();
            if (ps == PlayingStatus.IS_PLAYING) {
                musicControls[2].setImage(musicControlIcons[2]);
            } else if (ps == PlayingStatus.PAUSED) {
                musicControls[2].setImage(musicControlIcons[4]);
            } else if (ps == PlayingStatus.INITIAL_LOAD || ps == PlayingStatus.STOPPED) {
                musicControls[2].setImage(musicControlIcons[4]);
            }
            playAgent.controlStatus(getOrderType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class RefreshProgressThread extends Thread {
        double currentProgress;
        double lastProgress;

        //float audioTotalLength = playAgent.agentGetTotalLength(selectedFile);

        private RefreshProgressThread() {
        }

        @Override
        public void run() {
            try {
                while (playerStage.isShowing() && musicLength != 0) {
                    currentProgress = playProgress.getMax() * playAgent.agentGetPlayedLength() / musicLength;
                    Platform.runLater(() -> {
                        lastProgress = playProgress.getValue();
                        playProgress.setValue(currentProgress);
                        String style = String.format("-fx-fill:linear-gradient(to right,#1199ee %f%%, #111111 %f%%);", currentProgress, lastProgress);
                        sliderMask.setStyle(style);
                    });
                    //noinspection BusyWait
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class MouseClickMusicItemCell implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
                if (mouseEvent.getClickCount() == 2) {
                    //System.out.println("切换歌曲!");
                    try {
                        int index = musicListViewUI.getSelectionModel().getSelectedIndex();
                        if (index != -1) {
                            selectedFile = playAgent.agentSetMusic(index);
                            loadMusicInfoToScreen();
                            playBackgroundInTransition();
                            resetControls();
                            musicListViewUI.getSelectionModel().clearSelection();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (mouseEvent.getClickCount() == 1) {
                    HBox selectedBox = musicListViewUI.getSelectionModel().getSelectedItem();
                    if(selectedBox != null) {
                        for (HBox contentBox : musicListViewUI.getItems())
                            for (index = 0; index < selectedBox.getChildren().size(); index++)
                                contentBox.getChildren().get(index).setStyle("-fx-style-fill:rgb(0,0,0)");
                        for (index = 0; index < selectedBox.getChildren().size(); index++)
                            (selectedBox.getChildren().get(index)).setStyle("-fx-text-fill:rgb(246,80,65)");
                    }
                }
        }
    }

    private class PlayMusic implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                PlayingStatus ps = playAgent.agentCheckPlayingStatus();
                if (ps == PlayingStatus.IS_PLAYING) {
                    musicControls[2].setImage(musicControlIcons[2]);
                } else if (ps == PlayingStatus.PAUSED) {
                    musicControls[2].setImage(musicControlIcons[4]);
                } else if (ps == PlayingStatus.INITIAL_LOAD || ps == PlayingStatus.STOPPED) {
                    musicControls[2].setImage(musicControlIcons[4]);
                }
                playAgent.controlStatus(getOrderType());
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
                if (playAgent.agentCheckPlayingStatus() == PlayingStatus.IS_PLAYING || playAgent.agentCheckPlayingStatus() == PlayingStatus.PAUSED) {
                    playAgent.toStop();
                }
                playAgent.agentSaveMusicList();
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
                loadMusicInfoToScreen();
                playBackgroundInTransition();
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
                loadMusicInfoToScreen();
                playBackgroundInTransition();
                resetControls();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class MouseClickAdd implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            onOpenFile();
        }
    }

    protected class MouseClickAddFolder implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                onOpenFolder();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected class MouseClickVolume implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            volumeControlBar.setVisible(!volumeControlBar.isVisible());
        }
    }

    protected class ControlVolume implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            playAgent.agentControlVolume((float) volumeControlBar.getValue());
        }
    }
    private class MouseClickRemove implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                int index = musicListViewUI.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    String res = playAgent.agentRemoveMusic(index);
                    MusicListArea.removeListViewContent(index);
                    System.out.println("已删除:" + res);
                    musicListViewUI.getSelectionModel().clearSelection();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class MouseClickOrder implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent mouseEvent) {
           switch (currentOrderType){
               case ORDER -> {playOrderControl.setImage(playOrderControlIcons[1]);currentOrderType= MusicPlayer.playOrderType.LOOP;}
               case LOOP -> {playOrderControl.setImage(playOrderControlIcons[2]);currentOrderType= MusicPlayer.playOrderType.RANDOM;}
               case RANDOM -> {playOrderControl.setImage(playOrderControlIcons[0]);currentOrderType= MusicPlayer.playOrderType.ORDER;}
           }
        }
    }

    protected class ControlPosition implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            try {
                playAgent.agentChangePosition(playProgress.getValue() / 100.0d);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}