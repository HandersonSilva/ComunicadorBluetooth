package com.hb.handersonsilva.comunicadorbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.hb.handersonsilva.comunicadorbluetooth.Ultil.Bluetooth;
import com.hb.handersonsilva.comunicadorbluetooth.Ultil.ListarDispositivosP;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter= null;
    Context context = null;
    private static final int ATIVA_BLUETOOTH =1;
    private static final int ABRIR_LISTA =2;
    final  boolean conexao = false;
    private  static String MAC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnVerificar = (Button)findViewById(R.id.button_verificar);
            Button btnConectar = (Button)findViewById(R.id.button_Conectar);

            context = getApplicationContext();


           //botão verificar
            btnVerificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bluetooth bluetooth = new Bluetooth(context);
                    mBluetoothAdapter = bluetooth.verificarBluetooth();

                    Intent ativarBluetooth = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(ativarBluetooth,ATIVA_BLUETOOTH);

                }
            });
            //botão conectar
            btnConectar.setOnClickListener(new View.OnClickListener() {
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
}
