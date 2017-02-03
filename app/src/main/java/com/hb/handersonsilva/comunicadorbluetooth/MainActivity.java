package com.hb.handersonsilva.comunicadorbluetooth;

import android.app.Activity;
import android.app.LoaderManager;
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
import android.widget.EditText;
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
    final  boolean conexao = true;
    private  static String MAC = null;
    static TextView statusMessage;
    static  TextView statusCliente;
    static TextView textRecebido;
    static  TextView textStatusSocket;
    ConnectThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnClientConexao= (Button)findViewById(R.id.button_Cliente_Conexao);
            Button btnConectar = (Button)findViewById(R.id.button_AtivarServer);
            Button btnEnviaMsg = (Button)findViewById(R.id.button_enviarMsg);
            statusMessage = (TextView)findViewById(R.id.textView_StatusM);
            statusCliente = (TextView)findViewById(R.id.textView_statusClient);
            textRecebido = (TextView)findViewById(R.id.textView_textRecebido);
            textStatusSocket = (TextView)findViewById(R.id.textView_statusSocket);
            context = getApplicationContext();

            //Solicita que o bluetooth seja ligado caso contrario fecha o app
            this.ativarBluetooth();
           //Instanciando a class ConnectThread

          btnClientConexao.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Button btnClientConexao= (Button)findViewById(R.id.button_Cliente_Conexao);
                 if(conexao){
                     if (opButton == 0) {
                         connect.cancel();
                         btnClientConexao.setText("Ativar Cliente");
                         opButton=1;



                     } else {

                         Intent abrirLista = new Intent(MainActivity.this, ListarDispositivosP.class);
                         startActivityForResult(abrirLista,ABRIR_LISTA);
                         opButton=0;
                     }
                 }
              }
          });

            //Botão que chama a thread

            btnConectar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Button btnConectar = (Button) findViewById(R.id.button_AtivarServer);

                    if (opButton == 0) {
                            connect.cancel();
                            btnConectar.setText("Ativar Server");
                            opButton=1;
                            connect = new ConnectThread();
                    } else {
                            connect = new ConnectThread();
                            connect.start();
                            if(connect.isAlive()){
                                btnConectar.setText("Desativar");
                                opButton =0;

                            }else {

                                btnConectar.setText("Ativar Server");
                                opButton =1;
                            }

                    }
                }
            });

            btnEnviaMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText myText = (EditText)findViewById(R.id.editText_textoSend);
                    String myTextString = myText.getText().toString();
                    byte[] data = myTextString.getBytes();//converter uma string em bytes
                    connect.write(data);
                }
            });
    }

  public void ativarBluetooth()
  {
      Bluetooth bluetooth = new Bluetooth(context);
      mBluetoothAdapter = bluetooth.verificarBluetooth();

      Intent ativarBluetooth = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);//ativar o bluetooth caso esteja desligado
      startActivityForResult(ativarBluetooth,ATIVA_BLUETOOTH);
  }    @Override
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
                    connect = new ConnectThread(MAC);
                    connect.start();
                    Button btnClientConexao= (Button)findViewById(R.id.button_Cliente_Conexao);
                    btnClientConexao.setText("Desativar");



                }else {
                    Toast.makeText(context,"Falha ao obter o MAC", Toast.LENGTH_SHORT).show();
                }
        }

    }


    //receber mensagem de ConnectThread
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
            else if(dataString.equals("---C"))
                statusMessage.setText("Servidor Ativado, Canal OK");
            else if(dataString.equals("---D")) {
                statusMessage.setText("Servidor desativado");
                statusCliente.setText("Cliente desconectado");

            } else if(dataString.equals("---CLIENT"))
                statusCliente.setText("Conectado em;"+MAC);
            else if(dataString.equals("---CLIERRO"))
                statusCliente.setText("Erro ao se conecta a "+MAC);
            else if (dataString.equals("---B"))
                statusMessage.setText("Servidor ativado(Socket Excluido)");
            else if (dataString.equals("---CONECTADO"))
                textStatusSocket.setText("CONECTADO COM "+MAC);
            else {

                textRecebido.setText(new String(data));
            }
        }
    };
}
