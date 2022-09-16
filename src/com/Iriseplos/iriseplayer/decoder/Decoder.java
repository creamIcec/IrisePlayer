package com.Iriseplos.iriseplayer.decoder;

import com.Iriseplos.iriseplayer.mp3agic.Mp3File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Decoder {
    static AudioInputStream mp3AIS;
    public static AudioInputStream decodeMP3(File fileForFormat, long skippedBytes) throws Exception {
        //理论上来说跳帧可以从这儿下手，不直接输入文件，而是输入一个流
        AudioInputStream tempIs = AudioSystem.getAudioInputStream(fileForFormat);
        AudioFormat format = tempIs.getFormat();
        Mp3File tempFileForFormat = new Mp3File(fileForFormat);
        /*SoundFileFormat sff = new SoundFileFormat();
        sff.showSoundFileFormat(MP3File);*/
        if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16,
                    format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false);
            tempIs.skip(skippedBytes);
            System.out.println(format.getFrameRate());
            mp3AIS = AudioSystem.getAudioInputStream(format,tempIs);
        }
        return mp3AIS;
    }
    public static AudioInputStream decodeWAV(File WAVFile) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(WAVFile);
    }
    //暂时弃用
    @Deprecated
    public static byte[] doDecodeAll(File MP3File) throws Exception {
        byte[] loadedAllBytes;
        AudioInputStream mp3AIS = AudioSystem.getAudioInputStream(MP3File);
        AudioFormat format = mp3AIS.getFormat();
        /*SoundFileFormat sff = new SoundFileFormat();
        sff.showSoundFileFormat(MP3File);*/
        if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16,
                    format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false);
            mp3AIS = AudioSystem.getAudioInputStream(format, mp3AIS);
        }
        loadedAllBytes = mp3AIS.readAllBytes();
        return loadedAllBytes;
    }
}
