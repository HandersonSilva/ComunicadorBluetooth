package com.hb.handersonsilva.comunicadorbluetooth.Ultil;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;

import com.hb.handersonsilva.comunicadorbluetooth.MainActivity;
import com.hb.handersonsilva.comunicadorbluetooth.R;

import java.io.File;

/**
 * Created by HB on 20/02/2017.
 */

public class EfeitoThread extends Thread{
    final int tempoDeEspera = 500;
    private   int i=0;
    boolean play;


    public EfeitoThread(boolean paly){
        this.play=paly;
    }

    public void run(){

        while (play){
            SystemClock.sleep(tempoDeEspera);
            toMainActivity("---M1".getBytes());
            SystemClock.sleep(tempoDeEspera);
            toMainActivity("---M2".getBytes());
            SystemClock.sleep(tempoDeEspera);
            toMainActivity("---M3".getBytes());
            SystemClock.sleep(tempoDeEspera);
            toMainActivity("---M0".getBytes());

        }

    }
    private void toMainActivity(byte[] data) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }
}
