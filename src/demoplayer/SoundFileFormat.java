package demoplayer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.time.Duration;

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

    /*//获取音频文件格式
    public void getSoundFileFormat(File soundFile) throws Exception {
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
*/
    public void showSoundFileFormat(File soundFile) throws Exception {
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
        if(soundIsBigEndian == true){System.out.println("音频数据的字节储存顺序为：big-endian");}
        else{System.out.println("音频数据的字节储存顺序为：littele-endian");}
        System.out.println("音频时长(四舍五入后)为：" + soundSecond.toSeconds() + " s");
        //System.out.println("音频时长为：" + soundSecond.toMinutes() + " min " + (soundSecond.toSeconds() - soundSecond.toMinutes() * 60) + " s");
        //System.out.println(soundSecond.toMinutes() + " : " + (soundSecond.toSeconds() - soundSecond.toMinutes() * 60));
        System.out.println("==============");
    }
}
