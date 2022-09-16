package demoplayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class SoundInputStream {
    //音频输入流相关===========================================================
    //保存音频输入流
    public static AudioInputStream soundInputStream = null;
    //保存音频输入流可无阻塞地读取（或跳过）的最大字节数
    private static int soundInputStreamMaxByte;
    //保存音频输入流里声音的音频数据格式
    protected static AudioFormat soundInputStreamSoundDataFormat = null;
    //保存音频输入流的帧长度
    private static long soundInputStreamFrameLength;

    //清空所有内容，释放内存
    protected static void clearAll(){
        soundInputStream = null;
        soundInputStreamSoundDataFormat = null;
    }

    //获取音频输入流
    public static void getSoundInputStream(File soundFile) throws Exception {
        soundInputStream = AudioSystem.getAudioInputStream(soundFile);
        soundInputStreamMaxByte = soundInputStream.available();
        soundInputStreamSoundDataFormat = soundInputStream.getFormat();
        soundInputStreamFrameLength = soundInputStream.getFrameLength();
    }
    public static void showSoundInputStream() throws Exception {
        System.out.println("音频输入流可无阻塞地读取（或跳过）的最大字节数为：" + soundInputStreamMaxByte + " byte 即 " + soundInputStreamMaxByte / 1024.0 + " kb 或 " + soundInputStreamMaxByte / 1024.0 / 1024 + " mb");
        System.out.println("音频输入流里声音数据的音频格式为：" + soundInputStreamSoundDataFormat);
        System.out.println("音频输入流的帧长度为：" + soundInputStreamFrameLength + " frame");
        System.out.println("==============");
    }
}

