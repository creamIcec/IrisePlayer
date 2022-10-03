package com.Iriseplos.iriseplayer.player.filesystem;

import com.Iriseplos.iriseplayer.decoder.Decoder;
import com.Iriseplos.iriseplayer.mp3agic.*;
import javafx.scene.image.Image;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Objects;


public class MusicLoader {
    //AudioInputStream可以从各种流(如文件)获取音频数据
    private static AudioInputStream soundInputStream;
    //保存一次性读取的全部mp3数据
    private static byte[] loadedAllBytes;
    //保存音频输入流可无阻塞地读取（或跳过）的最大字节数
    //private static Integer soundInputStreamMaxByte;
    //保存音频输入流里声音的音频数据格式
    private static AudioFormat soundInputStreamSoundDataFormat = null;
    //保存音频输入流的帧长度
    private static Long soundInputStreamFrameLength;

    //保存以此音频数据格式的声音，每秒播放或录制的帧数,即帧速率 frame/s
    //private static float soundFrameRate;
    //保存音频数据每帧的大小以字节为单位 frame/byte
    //private static float soundFrameSize;
    //保存音频数据大小，以帧为单位
    //protected static long soundDataFrameLength;
    //private static AudioFileFormat soundFileFormat;
    //private static AudioFormat soundDataFormat;
    //private static AudioFileFormat.Type soundFileType;
    //private static int soundFileLength;
    //private static float soundDataByteLength;
    //private static int soundChannels;
    //private static AudioFormat.Encoding soundEncoding;
    //private static float soundSampleRate;
    //private static int soundSampleSizeBits;
    //private static boolean soundIsBigEndian;
    //private static Duration soundSecond;

    public SourceDataLine line;

    public Line getLineFromAudioSystem(){
        //line是什么?
        //获取某个音频格式的各种参数
        //line依赖于初次加载，后期应该可以不用
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,soundInputStreamSoundDataFormat);
        if(!AudioSystem.isLineSupported(info)){
            //应该是对info保存的各个参数进行判断，判断是否可以被读取
            System.out.println("不支持的格式?");
        }
        try{
            //以info的格式获取一个line?
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(getSoundInputStreamFormatFromFile());
            line.start();
            return line;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.out.println("跳帧过度?");
            throw new RuntimeException(e);
        }
    }
    public AudioFormat getSoundInputStreamFormatFromFile() throws UnsupportedAudioFileException, IOException {
        soundInputStreamSoundDataFormat = soundInputStream.getFormat();
        return soundInputStreamSoundDataFormat;
    }
    public AudioInputStream getSoundInputStreamFromMusic(Music music) throws Exception {
        return seekPosition(music,0);
    }
    /*public byte[] getLoadedAllBytesFromMusicMp3(Music music) throws Exception {
        soundInputStream = Decoder.decodeMP3(music.getMusicFile());
        loadedAllBytes = Decoder.doDecodeAll(music.getMusicFile());
        return loadedAllBytes;
    }*/
    public AudioInputStream seekPosition(Music music, long skippedBytes) throws Exception {
        if(Objects.equals(getFileExtension(music.getMusicFile()), "wav")){
            soundInputStream = Decoder.decodeWAV(music.getMusicFile(),skippedBytes);
            return soundInputStream;
        }else if(Objects.equals(getFileExtension(music.getMusicFile()), "mp3")){
            System.out.println("当前播放文件的比特长度:"+getTotalByteLength(music.getMusicFile()));
            soundInputStream = Decoder.decodeMP3(music.getMusicFile(),skippedBytes);//流式解码mp3
            return soundInputStream;
        }else{
            return null;
        }
    }
    /*public AudioInputStream getSoundInputStreamFromDecoder(AudioInputStream inputStream){

    }*/
    /*public static float getMusicFileLength(File soundFile) throws Exception {
        //soundFileFormat = AudioSystem.getAudioFileFormat(soundFile);
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
        return soundDataByteLength;
    }*/
    public static long getSoundFrameLengthWAV(File soundFile) throws Exception {
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(soundFile);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        //AudioFormat audioFormat = audioInputStream.getFormat();// 或者audioFileFormat.getFormat()
        System.out.println("总帧数:"+audioFileFormat.getFrameLength());
        return audioFileFormat.getFrameLength();
    }


    public static long getSoundFrameLengthMP3(File _Mp3file) throws InvalidDataException, UnsupportedTagException, IOException {
        Mp3File _File = new Mp3File(_Mp3file);
        System.out.println("总帧数:"+_File.getFrameCount());
        return _File.getFrameCount();
    }
    public static int getSoundBitRateMP3(Mp3File _Mp3File) {
        System.out.println("比特率:"+1000*_Mp3File.getBitrate());
        return 1000*_Mp3File.getBitrate();
    }
    public void clearAll(){
        soundInputStreamSoundDataFormat = null;
        line = null;
    }
    public void stopLine() throws IOException {
        line.drain();
        line.close();
        soundInputStream.close();
        clearAll();
    }
    public static String getFileExtension(File selectedFile){
        String fileName = selectedFile.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if("wav".equalsIgnoreCase(ext)){
            return "wav";
        }else if("mp3".equalsIgnoreCase(ext)){
            return "mp3";
        }else{
            System.out.println("文件格式不正确");
            return null;
        }
    }

    public static void testGetMp3InfoUsingMagic(File soundFile) throws InvalidDataException, UnsupportedTagException, IOException {
        Mp3File _mp3 = new Mp3File(soundFile);
        int frameCount = _mp3.getFrameCount();  //总帧数
        long sampleRate = _mp3.getSampleRate(); //采样率
        String channel = _mp3.getChannelMode(); //通道数
        System.out.println(frameCount);
    }

    public static Image getMusicAlbumIcon(File musicFile) throws IOException, InvalidDataException, UnsupportedTagException {
        Image albumImage;
        if(Objects.equals(MusicLoader.getFileExtension(musicFile), "mp3")) {
            Mp3File mp3file = new Mp3File(musicFile);
            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                byte[] imageData = id3v2Tag.getAlbumImage();
                if (imageData != null) {
                    //String mimeType = id3v2Tag.getAlbumImageMimeType();
                    // Write image to file - can determine appropriate file extension from the mime type
                    InputStream input = new ByteArrayInputStream(imageData);
                    albumImage = new Image(input);
                    input.close();
                } else {
                    albumImage = new Image(Objects.requireNonNull(MusicLoader.getAlbumImage("testImage/testImage.png")));
                }
            } else {
                albumImage = new Image(Objects.requireNonNull(MusicLoader.getAlbumImage("testImage/testImage.png")));
            }
        }else{
            albumImage = new Image(Objects.requireNonNull(MusicLoader.getAlbumImage("testImage/testImage.png")));
        }
        return albumImage;
    }

    /* 以下记录一些网上收集到的资料:
         {mp3 每帧均为1152个字节， 则：
         frame_duration = 1152 * 1000000 / sample_rate
         例如：sample_rate = 44100HZ时， 计算出的时长为26.122ms，这就是经常听到的mp3每帧播放时间固定为26ms的由来。}
         {一秒钟数据量(Byte) = 采样率√*采样通道√*位深度/8}
     */
    public static void main(String[] args) throws Exception {
        //System.out.println(testGetInfo(new File("C:\\Users\\Iriseplos\\Music\\cloudmusic\\Fells - Tell Me It's Okay.mp3")));
        File soundFile = new File("C:\\Users\\Iriseplos\\Music\\cloudmusic\\Fells - Tell Me It's Okay.mp3");
        testGetMp3InfoUsingMagic(soundFile);
        //testGetInfo(soundFile);
    }

    public static String getAlbumImage(String URL){
        try {
            return Objects.requireNonNull(MusicLoader.class.getResource(URL)).toExternalForm();
        }catch(NullPointerException exception){
            exception.printStackTrace();
            return null;
        }
    }

    public static ID3v1 getMp3Info(Mp3File mp3File){
        if (mp3File.hasId3v1Tag()) {
            return mp3File.getId3v1Tag();
        }else {
            return null;
        }
    }

    public static ID3v2 getMp3ID3v2Info(Mp3File mp3File){
        if (mp3File.hasId3v2Tag()) {
            //以下代码用于获取专辑封面图片，暂不启用
            /*byte[] albumImageData = id3v2Tag.getAlbumImage();
            if (albumImageData != null) {
                System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
            }*/
            return mp3File.getId3v2Tag();

        }else return null;
    }

    public static long getTotalByteLength(File musicFile) throws Exception{
        long totalByteLength;
        if((Objects.equals(MusicLoader.getFileExtension(musicFile), "mp3"))){
            Mp3File _mp3File = new Mp3File(musicFile);
            totalByteLength = _mp3File.getLength();
            return totalByteLength;
        }else if((Objects.equals(MusicLoader.getFileExtension(musicFile), "wav"))){
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(musicFile);
            totalByteLength = (long) audioFileFormat.getFormat().getFrameSize() * audioFileFormat.getFrameLength();
        }else{
            //暂不支持其他格式
            totalByteLength = 999;
        }
        System.out.println("总比特数:" + totalByteLength);
        return totalByteLength;
    }
}
