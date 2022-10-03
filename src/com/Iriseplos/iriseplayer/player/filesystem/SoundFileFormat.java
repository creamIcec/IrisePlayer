package com.Iriseplos.iriseplayer.player.filesystem;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.tritonus.share.TDebug.out;

public class SoundFileFormat {
    //音频文件相关===========================================================
    //保存音频文件格式
    private static AudioFileFormat soundFileFormat = null;
    //保存音频文件类型，如 WAV
    private static AudioFileFormat.Type soundFileType = null;
    //保存音频文件大小，以字节位为单位
    private static int soundFileLength;
    //保存音频数据格式
    private static AudioFormat soundDataFormat = null;
    //保存音频数据大小，以帧为单位
    protected static int soundDataFrameLength;
    //保存音频数据大小，以字节为单位
    private static int soundDataByteLength;
    //保存以此音频数据格式的声音，每秒播放或录制的帧数,即帧速率 frame/s
    private static float soundFrameRate;
    //保存音频数据每帧的大小以字节为单位 frame/byte
    private static int soundFrameSize;
    //保存以此音频数据格式的音频通道数（1表示单声道，2表示立体声）
    private static int soundChannels;
    //保存以此音频数据格式的声音编码类型
    private static AudioFormat.Encoding soundEncoding = null;
    //保存以此音频数据格式的声音，每秒播放或录制的样本数，即采样率 frame/s。
    protected static float soundSampleRate;
    //保存音频数据每个样本的大小以字节为单位 个/byte
    private static int soundSampleSizeBits;
    //保存音频数据的字节储存顺序
    private static boolean soundIsBigEndian;
    //保存声音秒数
    private static Duration soundSecond = null;

    //清空所有内容，释放内存
    protected static void clearAll(){
        soundFileFormat = null;
        soundFileType = null;
        soundDataFormat = null;
        soundEncoding = null;
        soundSecond = null;
    }

    //获取音频文件格式
    protected static void getSoundFileFormat(File soundFile) throws Exception {
        soundFileFormat = AudioSystem.getAudioFileFormat(soundFile);
        soundDataFormat = soundFileFormat.getFormat();
        soundFileType = soundFileFormat.getType();
        soundFileLength = soundFileFormat.getByteLength();
        soundDataFrameLength = soundFileFormat.getFrameLength();
        soundFrameRate = soundDataFormat.getFrameRate();
        soundFrameSize = soundDataFormat.getFrameSize();
        soundDataByteLength = soundFrameSize * soundDataFrameLength;
        soundChannels = soundDataFormat.getChannels();
        soundEncoding = soundDataFormat.getEncoding();
        soundSampleRate = soundDataFormat.getSampleRate();
        soundSampleSizeBits = soundDataFormat.getSampleSizeInBits();
        soundIsBigEndian = soundDataFormat.isBigEndian();
        soundSecond = Duration.ofSeconds(Math.round(soundDataFrameLength / soundSampleRate));
    }
    public static int getSoundFrameSizeInBits(File soundFile) throws UnsupportedAudioFileException, IOException {
        soundFileFormat = AudioSystem.getAudioFileFormat(soundFile);
        return soundFileFormat.getFormat().getSampleSizeInBits();
    }
    public static long getSoundLengthInSeconds(File soundFile) throws UnsupportedAudioFileException, IOException {
        soundFileFormat = AudioSystem.getAudioFileFormat(soundFile);
        soundDataFormat = soundFileFormat.getFormat();
        soundDataFrameLength = soundFileFormat.getFrameLength();
        soundSampleRate = soundDataFormat.getSampleRate();
        return Duration.ofSeconds(Math.round(soundDataFrameLength / soundSampleRate)).toSeconds();
    }
    protected static void showSoundFileFormat(){
        System.out.println("==============");
        System.out.println("音频文件大小为：" + soundFileLength + " byte 即 " + soundFileLength / 1024.0 + " kb 或 " + soundFileLength / 1024.0 / 1024.0 + " mb");
        System.out.println("音频文件类型为：" + soundFileType);
        System.out.println("音频数据帧长度为：" + soundDataFrameLength + " frame");
        System.out.println("以此音频数据格式声音的帧速率为：" + soundFrameRate + " frame/s");
        System.out.println("音频数据每帧的大小为：" + soundFrameSize + " byte");
        System.out.println("音频数据的大小为：" + soundDataByteLength + " byte 即 " + soundDataByteLength / 1024.0 + " kb 或 " + soundDataByteLength / 1024.0 / 1024.0 + " mb");
        if(soundChannels == 1){System.out.println("以此音频数据格式的音频通道为1：单声道" );}
        else{System.out.println("以此音频数据格式的音频通道为2：立体声" );}
        System.out.println("以此音频数据格式的声音编码类型为：" + soundEncoding);
        System.out.println("以此音频数据格式声音的采样率为：" + soundSampleRate + " frame/s");
        System.out.println("每个样本的位数为：" + soundSampleSizeBits + " bit");
        if(soundIsBigEndian){System.out.println("音频数据的字节储存顺序为：big-endian");}
        else{System.out.println("音频数据的字节储存顺序为：little-endian");}
        System.out.println("音频时长(四舍五入后)为：" + soundSecond.toSeconds() + " s");
        // System.out.println("音频时长为：" + soundSecond.toMinutes() + " min " + (soundSecond.toSeconds() - soundSecond.toMinutes() * 60) + " s");
        // System.out.println(soundSecond.toMinutes() + " : " + (soundSecond.toSeconds() - soundSecond.toMinutes() * 60));
        System.out.println("==============");
    }
    //以下方法用于测试获取音频信息，请在本类的main方法中单独调用
    //同时适用于MP3和WAV文件
    public static float testGetInfo(File soundFile) throws Exception {
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(soundFile);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat audioFormat = audioInputStream.getFormat();// 或者audioFileFormat.getFormat()
        out("--------信息开始--------");
        out("---------AudioFileFormat---------");
        out("Type " + audioFileFormat.getType());
        out("byteLength " + audioFileFormat.getByteLength());
        out("frame length " + audioFileFormat.getFrameLength());
        out("format " + audioFileFormat.getFormat());
        out("properties " + audioFileFormat.properties());
        out("--------------AudioInputStream-------");
        out("frameLength " + audioInputStream.getFrameLength());
        out("--------AudioFormat----------");
        out("encoding " + audioFormat.getEncoding());
        out("channels " + audioFormat.getChannels());
        out("sampleRate " + audioFormat.getSampleRate());
        out("frameRate " + audioFormat.getFrameRate());
        out("properties " + audioFormat.properties());
        out("sampleSizeInBits " + audioFormat.getSampleSizeInBits());
        out("frameSize " + audioFormat.getFrameSize());
        out("--------信息结束----------");
        return audioFileFormat.getFrameLength();
    }

    public static void main(String[] args) throws Exception {
        SoundFileFormat.getSoundFileFormat(new File("C:\\Users\\Iriseplos\\Music\\cloudmusic\\Aimer - Ref_rain.mp3"));
        SoundFileFormat.showSoundFileFormat();
    }
}