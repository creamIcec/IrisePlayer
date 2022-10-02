package com.Iriseplos.iriseplayer.player;


import com.Iriseplos.iriseplayer.agent.Agent;
import com.Iriseplos.iriseplayer.mp3agic.InvalidDataException;
import com.Iriseplos.iriseplayer.mp3agic.UnsupportedTagException;
import com.Iriseplos.iriseplayer.player.filesystem.Music;
import com.Iriseplos.iriseplayer.player.filesystem.MusicList;
import com.Iriseplos.iriseplayer.player.filesystem.MusicLoader;
import com.Iriseplos.iriseplayer.renderer.Start;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class MusicPlayer {
    private static AudioInputStream inputStream;
    private static Thread playThread;   //播放音频的任务主线程（音频数据读取输出）
    private static Thread pauseThread;   //暂停音频线程
    FloatControl gainControl;
    private boolean isPlaying = false;
    private boolean isStopped = true;
    private boolean isPaused = false;
    private MusicLoader Ml = new MusicLoader();
    private File musicFile;
    private long playedBytes;
    private Music currentMusic;
    private Line currentLine;
    private boolean isAutoNext;
    private final Agent playAgent = Start.getAgent();
    private boolean istoChange = false;

    public MusicPlayer(Music music) throws IOException, InvalidDataException, UnsupportedTagException{
        this.getMusicFile(music);
        if (Objects.equals(MusicLoader.getFileExtension(musicFile), "mp3")) {
            currentMusic = new Music(this.musicFile);
        }
    }

    public MusicPlayer(){}

    //TODO 实现跳帧

    public static void main(String[] args) {
        //new MusicPlayer().start(true);
    }

    private void playMusic(long playPos) throws Exception {
        this.isPlaying = true;
        //MusicHandler Mh = new MusicHandler();
        if (inputStream == null) {
            inputStream = Ml.getSoundInputStreamFromMusic(this.currentMusic);   //流式传输inputStream
        } else {
            inputStream = Ml.seekPosition(this.currentMusic, playPos);
        }
        System.out.println("从"+playPos+"字节处开始播放");
        playedBytes = MusicLoader.getSoundFrameLengthMP3(currentMusic.getMusicFile())*4608*playPos/MusicLoader.getTotalByteLength(currentMusic.getMusicFile());
        //long res = inputStream.available();
        //System.out.println(res);
        //loadedMusicInput = new byte[inputStream.available()];
        /*while((loadingCount = inputStream.read(loadedMusicInput,0, loadedMusicInput.length)) != -1){
            System.out.println("读取:"+loadingCount);
        };*/
        //currentClip = AudioSystem.getClip();   Clip相关
        //currentClip.open(inputStream);   Clip相关
        //System.out.println(inputStream.getFormat());   输出语句
        //loadedMusicInput = Ml.getLoadedAllBytesFromMusicMp3(this.currentMusic);
        if (currentLine != null) {
            currentLine = null;
        }
        currentLine = Ml.getLineFromAudioSystem();
        //System.out.println(inputStream.markSupported());   输出语句

        /*--------------IMPORTANT  以下为流式播放核心部分---------------*/
        //记录是否循环播放
        byte[] tempBuffer = new byte[1024];
        //音频输入流读取指定最大大小（tempBuffer.length）数据并传输到源数据线，off为传输数据在字节数组（tempBuffer）开始保存数据的位置，最终读取大小保存进count
        long count;
        while ((count = inputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {   //流式传输
            //System.out.println("读取"+count);
        /*int i=0;
        while(i < loadedMusicInput.length){
            for(int p=1; p<=4;p++) {
                if(i==1023) break;
                tempBuffer[i] = loadedMusicInput[i];
                i++;
            }
            count=4;*/
            //System.out.println("音频输入流读取了" + count + "个字节");
            //inputStream.mark();
            synchronized (this) {
                while (!this.isPlaying) {
                    System.out.println("暂停!");
                    wait();
                }
                //源数据线进行一次数据输出，将从字节数组中位置off开始将读取到的数据输出到缓冲区（输出大小为count）（混音器从混音器读取数据播放）
                //播放语句
                Ml.line.write(tempBuffer, 0, (int) count);
                playedBytes += count;
                //System.out.println("总字节数:"+playedBytes);
            }
            //if(i==1023) i=0;
        }
        /*--------------IMPORTANT  以上为流式播放核心部分---------------*/
        /*--------------IMPORTANT 以下为静态播放核心部分----------------*/
        /*currentClip.start();
        while(currentClip.getFramePosition() != currentClip.getFrameLength()) {
            synchronized (currentClip) {
                while (!this.isPlaying) {
                    System.out.println("暂停!");
                    lastFrameBeforePaused = currentClip.getFramePosition();
                    currentClip.stop();
                    Thread.wait();
                }
            }
        }*/
        /*--------------IMPORTANT 以上为静态播放核心部分----------------*/
        boolean flagNext = checkIfAutoNext();
        if (!flagNext) {
            stopMusic();
        } else {
            if (playAgent.agentGetMusicList().indexOf(currentMusic.getMusicFile().getAbsolutePath()) == MusicList.getTotal() - 1) {
                this.currentMusic = MusicList.generateMusic(0);
            } else {
                this.currentMusic = MusicList.generateMusic(playAgent.agentGetMusicList().indexOf(currentMusic.getMusicFile().getAbsolutePath()) + 1);
            }
            playAgent.agentSetAutoChanged();
            start(true,  0);
        }
    }
    public boolean checkIfAutoNext() {
        return isAutoNext;
    }

    public void start(boolean autoNext, long playPos) throws Exception {
        if (inputStream != null) {
            if (!istoChange) {
                System.out.println("切换歌曲");
            }
        }
        isAutoNext = autoNext;
        playThread = new Thread(() -> {
            try {
                playMusic(playPos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    //外部调用控制方法：暂停音频线程
    public void pauseMusic() {
        pauseThread = new Thread(() -> {
            System.out.println("调用暂停");
            pause();
        });
        pauseThread.start();
    }
    public void continueMusic() {
        continue_();
    }

    private void pause() {
        synchronized (this) {
            this.isPlaying = false;
            this.isPaused = true;
            notifyAll();
        }
    }

    private void continue_() {
        synchronized (this) {
            this.isPlaying = true;
            this.isPaused = false;
            notifyAll();
            System.out.println("继续!");
        }
    }

    public void stopMusic() throws Exception {
        synchronized (this) {
            System.out.println("播放结束");
            //清空数据行并关闭流等全部与音频播放相关内容
            Ml.stopLine();
            Ml = null;
            if (this.isPlaying) {
                isPlaying = false;
                isPaused = false;
                isStopped = true;
                playThread.interrupt();
            } else {
                pauseThread.interrupt();
                playThread.interrupt();
            }
            if(istoChange)
                Ml = new MusicLoader();
        }
    }

    public void changePosition(double playPercentage) throws Exception {
        istoChange = true;
        stopMusic();
        start(true,(long) (this.playAgent.agentGetTotalByteLength(this.currentMusic.getMusicFile()) * playPercentage));
    }

    public void getMusicFile(Music music) {
        this.musicFile = music.getMusicFile();
    }

    public long getPlayedBytes() {
        return playedBytes;
    }

    //TODO:明确4的来历及其是否为定值
    public int getPlayedFramesMp3() {
            //"4608"是怎么来的?
        int playedFrames = (int) (playedBytes / 4608);
            System.out.println("已播放帧数:" + playedFrames);
            return playedFrames;
    }
    public Music getCurrentPlayingMusic() {
        return currentMusic;
    }

    public File getCurrentPlayingFile() {
        return currentMusic.getMusicFile();
    }

    /* TODO:找到一个方法使得MP3文件的进度显示正常
        思路: 已播放比特数/每帧大小(定值) = 已播放帧数
             已播放帧数/总帧数 = 进度
             所以: 进度 = 已播放比特数 / 每帧大小(Mp3为4068) / 总帧数 */
    /*private class CountTime extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                try {
                    playedFrames += 1;
                    System.out.println("已播放" + playedFrames);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/
    public PlayingStatus checkPlayingStatus() {
        if (isPlaying && !isPaused) {
            return PlayingStatus.IS_PLAYING;
        } else if (!isPlaying && isPaused) {
            return PlayingStatus.PAUSED;
        } else if (isStopped) {
            return PlayingStatus.STOPPED;
        } else {
            return null;
        }
    }

    /*TODO 添加自动根据音频文件最大音量调整初始播放声音的功能
        参考: gainControl.getValue();
    */
    public void controlVolume(float delta) {
        synchronized (this) {
            gainControl = (FloatControl) currentLine.getControl(FloatControl.Type.MASTER_GAIN);
            System.out.println("当前音量:" + gainControl.getValue());
            gainControl.setValue(delta);
        }
    }
}
