package demoplayer;

import java.util.Scanner;

public class MusicPlayer {

    public static void main(String[]args) throws Exception {
        SoundPlayer musicPlayer = new SoundPlayer();
        Scanner sc = new Scanner(System.in);
        musicPlayer.setSoundFilePath("D:\\Programming\\Java\\IrisePlayer\\MitiS; RUNN - Shattered (feat. RUNN).wav");
        musicPlayer.start(true);//开始播放
        if(sc.nextInt() == 1) {
            musicPlayer.pause();//暂停播放
            if(sc.nextInt() == 2){
                musicPlayer.continues();
            }
        }
        //musicPlayer.continues();继续播放
        //musicPlayer.stop();停止播放

        //musicPlayer.setSoundFilePath("C:\\Users\\17512\\Desktop\\bgm2.wav");//更换音乐
        //musicPlayer.start(true);//开始播放（切歌）

    }
}

