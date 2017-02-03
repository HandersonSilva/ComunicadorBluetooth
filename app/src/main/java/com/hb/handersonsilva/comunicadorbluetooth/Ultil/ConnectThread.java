package com.hb.handersonsilva.comunicadorbluetooth.Ultil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import com.hb.handersonsilva.comunicadorbluetooth.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import static android.R.id.input;

/**
 * Created by HB on 26/01/2017.
 */

public class ConnectThread extends Thread{

    BluetoothServerSocket myServerSocket =null;
    BluetoothSocket myBtSocket =null;
    BluetoothAdapter myBluetoothAdapter=null;
    InputStream inputData = null;
    OutputStream outputData = null;
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
         myBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        if(this.server){
            //Sever

            try {
                //O objetivo do soquete do servidor é ouvir solicitações
                // de conexão de entrada e, quando uma for aceita, fornecer um BluetoothSocket conectado.
                myServerSocket = myBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Comunicador Bluetooth", UUID.fromString(myUUID));


                toMainActivity("---C".getBytes());
                myBtSocket = myServerSocket.accept();

                //excluindo o Server
                if(myBtSocket!= null) {
                    myServerSocket.close();
                    toMainActivity("---B".getBytes());

                }

            }catch (IOException e){
                      /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                    Envia um código para a Activity principal, informando que
                a conexão falhou.
                 */
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }else {
            //Cliente
            try {

                //criando a comunicação cliente
                BluetoothDevice btDevice = myBluetoothAdapter.getRemoteDevice(btAdress);
                myBtSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));
                //Cancelar descoberta
               // myBluetoothAdapter.cancelDiscovery();


                //conectando .....
                if(myBtSocket!=null){
                    myBtSocket.connect();
                    toMainActivity("---CLIENT".getBytes());
                }


            }catch (IOException erro){
                erro.printStackTrace();
                toMainActivity("---CLIERRO".getBytes());
            }

        }

        if(myBtSocket!=null){
            //Mandar mensagem para activity principal avisando que estar conectado
            toMainActivity("---CONECTADO".getBytes());

            try {
                  /*  Obtem referências para os fluxos de entrada e saída do
                socket Bluetooth.
                 */
                 inputData = myBtSocket.getInputStream();
                 outputData = myBtSocket.getOutputStream();

                /*  Cria um byte array para armazenar temporariamente uma
                mensagem recebida.
                    O inteiro bytes representará o número de bytes lidos na
                última mensagem recebida.
                 */
                 byte[] buffer = new byte[1024];
                 int bytes;

                //permanece escutando
                while(running) {

                    bytes = inputData.read(buffer);
                    toMainActivity(Arrays.copyOfRange(buffer, 0, bytes));

                }

            }catch (IOException erro){

            }



        }
    }
        //funcão que enviar mensagem para o outro dispositivo
    public void write(byte[] data) {

        if(outputData != null) {
            try {

                /*  Transmite a mensagem.
                 */
                outputData.write(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            /*  Envia à Activity principal um código de erro durante a conexão.
             */
            toMainActivity("---N".getBytes());
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
            if(myServerSocket!=null){
                myServerSocket.close();
            }

            if(myBtSocket!=null){
                myBtSocket.close();
            }
            toMainActivity("---D".getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }
}
