package demoplayer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundSourceDataLine {
    //源数据线===========================================================
    public static DataLine.Info dataLineInformationl = null;//保存数据线信息
    public static SourceDataLine sourceDataLine = null;//保存源数据线

    //清空所有内容，释放内存
    protected static void clearAll(){
        dataLineInformationl = null;
        sourceDataLine = null;
    }

    //加载源数据线
    public static void loadSourceDataLine() throws Exception {
        //定义数据线信息，即包装音频信息（记录数据线类，音频数据格式，缓冲区大小）
        dataLineInformationl = new DataLine.Info(SourceDataLine.class, SoundInputStream.soundInputStreamSoundDataFormat);
        //获得源数据线
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInformationl);
        //设置源数据线打开的音频数据格式和源数据线的缓冲区大小(可指定)
        sourceDataLine.open(SoundInputStream.soundInputStreamSoundDataFormat);
        //源数据线开始工作
        sourceDataLine.start();
    }
}
