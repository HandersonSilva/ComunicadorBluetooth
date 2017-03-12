package com.hb.handersonsilva.comunicadorbluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.handersonsilva.comunicadorbluetooth.Ultil.Bluetooth;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.ConnectThread;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.EfeitoThread;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.ListarDispositivosP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.data;
import static android.R.attr.delay;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter= null;
    Context context = null;
    private static final int ATIVA_BLUETOOTH =1;
    private static final int ABRIR_LISTA =2;
    private  int opButton = 1;
    private boolean startRec=false;
    final  boolean conexao = true;
    private  static String MAC = null;
    static TextView statusMessage;
    static  TextView statusCliente;
    static  TextView textStatusSocket;
    static TextView statusconexao;
    ConnectThread connect;
    static EfeitoThread efeitoImageView;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private  boolean cont=true;
    private MediaPlayer mp=null;
    private  boolean play;
    static boolean conectado=false;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static   File tempMp3;
    static ImageView mostrarAudio;
    //final Handler handle = new Handler(Looper.getMainLooper());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnClientConexao= (Button)findViewById(R.id.button_Cliente_Conexao);
            Button btnConectar = (Button)findViewById(R.id.button_AtivarServer);
            Button btnGravarAudio = (Button)findViewById(R.id.btn_gravarAudio);

            statusMessage = (TextView)findViewById(R.id.textView_StatusM);
            statusCliente = (TextView)findViewById(R.id.textView_statusClient);
            textStatusSocket = (TextView)findViewById(R.id.textView_statusSocket);
            statusconexao = (TextView)findViewById(R.id.textView_statusConexao);
            mostrarAudio = (ImageView)findViewById(R.id.imageView_mostrarAudio);
            context = getApplicationContext();

            //Solicita que o bluetooth seja ligado caso contrario fecha o app
            this.ativarBluetooth();

            //Setar o caminho onde sera salvo o arquivo de audio
            mFileName = getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest.mp3";
          //Caminho do audio temporario para reprodução
            try {
                tempMp3 = File.createTempFile("testePlay", "mp3", getCacheDir());
            }catch (IOException erro)
            {
                String s = erro.toString();
                erro.printStackTrace();
            }

            //Efeito para quando receber o audio
            play = true;
            efeitoImageView = new EfeitoThread(play);
            efeitoImageView.start();


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

        btnGravarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnPressGravar = (Button)findViewById(R.id.btn_gravarAudio);

                if(startRec){
                    somPrarGravar();
                    btnPressGravar.setBackgroundResource(R.drawable.btn_gravar1142);
                    stopRecording();
                    Toast.makeText(getApplicationContext(),"Caminho"+mFileName,Toast.LENGTH_LONG).show();
                    startRec=false;
                }else {
                    boolean c = true;

                    somGravar();
                    btnPressGravar.setBackgroundResource(R.drawable.btn_gravar2142);

                    startRecording();

                    startRec=true;
                }

            }
        });



    }
    //efeito do som
public void somGravar(){
    mp= MediaPlayer.create(this, R.raw.gravando);
    if(mp.isPlaying()){
        mp.stop();
    }else {
        mp.start();
    }

}
    public void somPrarGravar(){
       mp= MediaPlayer.create(this, R.raw.stopgravacao);
        if(mp.isPlaying()){
            mp.stop();
        }else {
            mp.start();
        }
    }


    //GRAVAR AUDIO
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//formato mp3
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    //PARAR A GRAVAÇÃO
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        //Pegar a gravação e transformar em bytes e enviar para ConnectThread
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            File aFile = new File(mFileName);
            InputStream is = new FileInputStream(aFile);
            byte[] temp = new byte[1024];
            int read;

            while((read = is.read(temp)) >= 0){
                buffer.write(temp, 0, read);
            }

            byte[] data = buffer.toByteArray();
            // process the data array . . .

            sendAudio(data);//Enviar para ConnectThread
        }catch (IOException erro ) {
            // TODO Auto-generated catch block
            erro.printStackTrace();
        }

   }
    //Enviar dados
    public void sendAudio(byte[] audioEnviar) {

        //if(conectado){
            byte[] data = audioEnviar;

            connect.write(data);
       // }
        //Toast.makeText(getApplicationContext(),"Audio não enviado",Toast.LENGTH_SHORT).show();
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



    //receber dados de ConnectThread e EfeitoThread
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
            else if (dataString.equals("---CONECTADO")){
                conectado =true;
                textStatusSocket.setText("CONECTADO COM "+MAC);
            } else if (dataString.equals("---M1")){
                mostrarAudio.setImageResource(R.drawable.mostraraudio1001);
            }else if (dataString.equals("---M2")){
                mostrarAudio.setImageResource(R.drawable.mostraraudio1002);
            }else if (dataString.equals("---M3")){
                mostrarAudio.setImageResource(R.drawable.mostraraudio1003);
            }else if (dataString.equals("---M0")){
               mostrarAudio.setImageResource(R.drawable.mostraraudio1000);
            }
            else if (dataString.equals("---CONECTADO")){
               // statusconexao.setText("Conectado Com"+MAC);
            }
            else {
                try {
                    //tempMp3.deleteOnExit();//Solicita que o arquivo ou diretório indicado por este caminho abstract ser excluído quando a máquina virtual termina.
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(data);
                    fos.close();

                    // resetting mediaplayer instance to evade problems

                    mediaPlayer.reset();

                    // In case you run into issues with threading consider new instance like:
                    // MediaPlayer mediaPlayer = new MediaPlayer();

                    // Tried passing path directly, but kept getting
                    // "Prepare failed.: status=0x1"
                    // so using file descriptor instead
                    FileInputStream fis = new FileInputStream(tempMp3);
                    mediaPlayer.setDataSource(fis.getFD());

                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException ex) {
                    String s = ex.toString();
                    ex.printStackTrace();
                }
        }
        }
    };
}
