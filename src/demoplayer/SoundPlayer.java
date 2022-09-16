package demoplayer;

import java.io.File;
//import java.time.Instant;

public class SoundPlayer {
    private static String soundFilePath = null;//保存为字符串类型的绝对地址，例："C:\\Users\\17512\\Desktop\\bgm.wav";
    private static File soundFile = null;//保存音频文件
    //========================================================

    private volatile boolean running = false;  //记录音频是否播放
    private static boolean isLoop = false; //记录是否循环播放
    private static Thread mainThread = null;   //播放音频的任务主线程（音频数据读取输出）
    private static Thread secondThread = null;   //播放音频的任务次线程（控制主线程暂停、继续）
    private static byte tempBuffer[] = null;//缓冲区
    private static int count;//保存得到的采样数
    //private static Instant startTime = null; 待后续开发
    //private static Instant endTime = null;待后续开发

    //清空所有内容，释放内存
    protected static void clearAll(){
        soundFile = null;
        mainThread = null;
        secondThread = null;
        tempBuffer = null;
        //startTime = null;待后续开发
        //endTime = null;待后续开发
    }
    //设置音频文件绝对路径
    public void setSoundFilePath(String absolutePath){
        soundFilePath = absolutePath;
    }

    private void playMusic(){
        try{
            synchronized(this){
                running = true;
            }
            //通过数据行读取音频数据流，发送到混音器;
            //数据流传输过程：AudioInputStream -> SourceDataLine;
            if(soundFilePath == null){
                System.out.println("未设置声音文件绝对路径");
                return;
            }

            soundFile = new File(soundFilePath);

            //MixerInformation.getMixerInformation();
            //MixerInformation.showMixerInformation();
            //SoundFileFormat.getSoundFileFormat(soundFile);
            //SoundFileFormat.showSoundFileFormat();

            SoundInputStream.getSoundInputStream(soundFile);
            //SoundInputStream.showSoundInputStream();

            SoundSourceDataLine.loadSourceDataLine();

            System.out.println("播放开始");
            tempBuffer = new byte[1024];
            //音频输入流读取指定最大大小（tempBuff.length）数据并传输到源数据线，off为传输数据在字节数组（tempBuffer）开始保存数据的位置，最终读取大小保存进count
            while((count = SoundInputStream.soundInputStream.read(tempBuffer,0,tempBuffer.length)) != -1){
                //System.out.println("音频输入流读取了" + count + "个字节");
                synchronized(this){
                    while(!running)
                        wait();
                }
                //源数据线进行一次数据输出，将从字节数组中位置off开始将读取到的数据输出到缓冲区（输出大小为count）（混音器从混音器读取数据播放）
                SoundSourceDataLine.sourceDataLine.write(tempBuffer,0,count);
            }

            stopMusic();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private void playMusic(boolean isLoop)throws Exception {
        try{
            if(isLoop){
                while(isLoop){
                    playMusic();
                }
            }else{
                playMusic();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    //暂停播放音频
    private void pauseMusic(){ synchronized(this){ running = false; notifyAll(); } }
    //继续播放音乐
    private void continueMusic(){ synchronized(this){ running = true; notifyAll(); } }
    //停止播放音频
    private void stopMusic() throws Exception {
        synchronized(this){
            running = false;
            isLoop = false;
            System.out.println("播放结束");
            //清空数据行并关闭
            SoundSourceDataLine.sourceDataLine.drain();
            SoundSourceDataLine.sourceDataLine.close();
            SoundInputStream.soundInputStream.close();
            SoundFileFormat.clearAll();
            SoundSourceDataLine.clearAll();
            SoundInputStream.clearAll();
            clearAll();
        }
    }

    //外部调用控制方法:生成音频主线程；
    public void start(boolean loop)throws Exception{
        if(SoundInputStream.soundInputStream != null || SoundSourceDataLine.sourceDataLine != null){
            System.out.println("切换歌曲");
            stopMusic();
        }
        isLoop = loop;
        mainThread = new Thread(new Runnable(){
            public void run(){ try { playMusic(isLoop); } catch (Exception e) { e.printStackTrace(); } }
        });
        mainThread.start();
    }

    //外部调用控制方法：暂停音频线程
    public void pause(){
        secondThread = new Thread(new Runnable(){
            public void run(){
                System.out.println("调用暂停");
                pauseMusic();
            }
        });
        secondThread.start();
    }
    //外部调用控制方法：继续音频线程
    public void continues(){
        secondThread = new Thread(new Runnable(){
            public void run(){
                System.out.println("调用继续");
                continueMusic();
            } });
        secondThread.start();
    }
    //外部调用控制方法：结束音频线程
    public void stop(){
        secondThread = new Thread(new Runnable(){
            public void run(){
                System.out.println("调用停止");
                try {stopMusic();}
                catch (Exception e) {e.printStackTrace();}
            } });
        secondThread.start();
    }
}

