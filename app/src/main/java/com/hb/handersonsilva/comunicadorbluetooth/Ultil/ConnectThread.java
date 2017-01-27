package com.hb.handersonsilva.comunicadorbluetooth.Ultil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import com.hb.handersonsilva.comunicadorbluetooth.MainActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by HB on 26/01/2017.
 */

public class ConnectThread extends Thread{

    BluetoothServerSocket myServerSocket =null;
    BluetoothSocket myBtSocket =null;
    String btAdress = null;
    String myUUID ="00001101-0000-1000-8000-00805F9B34FB";
    Boolean server = false;
    Boolean running = false;

    //Construto do server
    public ConnectThread(){
        this.server = true;

    }

    //Construtor cliente
    public  ConnectThread (String mac){
        this.server =false;
        this.btAdress = mac;
    }

    public  void run(){
        this.running = true;
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(this.server){

            try {
                //O objetivo do soquete do servidor é ouvir solicitações
                // de conexão de entrada e, quando uma for aceita, fornecer um BluetoothSocket conectado.
                myServerSocket = myBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Comunicador Bluetooth", UUID.fromString(myUUID));
                while (running){
                    try {
                        myBtSocket = myServerSocket.accept();
                    }catch (IOException e){
                        break;
                    }
                    //excluindo o Server
                    if(myBtSocket!= null){
                        myServerSocket.close();
                        break;
                    }
                }


            }catch (IOException e){
                      /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                    Envia um código para a Activity principal, informando que
                a conexão falhou.
                 */
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }
    }


    private void toMainActivity(byte[] data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }
    public void cancel() {

        try {

            running = false;
            myServerSocket.close();
            if(myBtSocket!=null){
                myBtSocket.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }
}
