package com.Iriseplos.iriseplayer;

import com.Iriseplos.iriseplayer.renderer.Start;


public class Init implements Runnable{
    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Start.run();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void main(String[] args) throws RuntimeException {
        new Init().run();
    }
}
