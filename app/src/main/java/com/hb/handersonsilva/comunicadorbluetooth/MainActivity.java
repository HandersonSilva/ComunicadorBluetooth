package com.hb.handersonsilva.comunicadorbluetooth;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.handersonsilva.comunicadorbluetooth.Ultil.Bluetooth;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.ConnectThread;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.ListarDispositivosP;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter= null;
    Context context = null;
    private static final int ATIVA_BLUETOOTH =1;
    private static final int ABRIR_LISTA =2;
    private  int opButton = 1;
    final  boolean conexao = false;
    private  static String MAC = null;
    static TextView statusMessage;
    static TextView textSpace;
    ConnectThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnVerificar = (Button)findViewById(R.id.button_verificar);
            Button btnProcurar = (Button)findViewById(R.id.button_Conectar);
            Button btnConectar = (Button)findViewById(R.id.button_AtivarServer);
            statusMessage = (TextView)findViewById(R.id.textView_StatusM);
            textSpace = (TextView)findViewById(R.id.textView_textData);
            context = getApplicationContext();


           //botão verificar
            btnVerificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bluetooth bluetooth = new Bluetooth(context);
                    mBluetoothAdapter = bluetooth.verificarBluetooth();

                    Intent ativarBluetooth = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);//ativar o bluetooth caso esteja desligado
                    startActivityForResult(ativarBluetooth,ATIVA_BLUETOOTH);

                }
            });
            //botão procurar
            btnProcurar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(conexao){
                        //desconectar
                    }else{
                        //conectar
                        Intent abrirLista = new Intent(MainActivity.this, ListarDispositivosP.class);
                        startActivityForResult(abrirLista,ABRIR_LISTA);
                    }
                }
            });
            //Botão que chama a thread
            connect = new ConnectThread();
            btnConectar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //colocar uma condição ao tentar startar o server bluetooth tem que esta ativo
                    Button btnConectar = (Button) findViewById(R.id.button_AtivarServer);

                    if (opButton == 0) {

                            connect.cancel();
                            btnConectar.setText("Ativar Server");
                            opButton=1;
                            connect = new ConnectThread();
                    } else {
                             connect.start();
                            if(connect.isAlive()){
                                if(connect.isInterrupted()){
                                    Toast.makeText(getApplicationContext(),"Ative seu Bluetooth",Toast.LENGTH_SHORT).show();
                                    btnConectar.setText("Ativar Server");
                                    opButton =1;
                                }else {
                                    btnConectar.setText("Desativar");
                                    opButton =0;
                                }

                            }else {

                                btnConectar.setText("Ativar Server");
                                opButton =1;
                            }





                    }




                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){

            case ATIVA_BLUETOOTH:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(context,"Bluetooth está ativado", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"Bluetooth não foi ativado o aplicativo foi fechado", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case ABRIR_LISTA:
                if(resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(ListarDispositivosP.ENDERECO_MAC);
                    Toast.makeText(context,"Mac recebido"+MAC, Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context,"Falha ao obter o MAC", Toast.LENGTH_SHORT).show();
                }
        }

    }


    //receber mensagem
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
            else {

                textSpace.setText(new String(data));
            }
        }
    };
}
