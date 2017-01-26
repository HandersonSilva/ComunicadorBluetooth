package com.hb.handersonsilva.comunicadorbluetooth.Ultil;

import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Handerson Silva on 24/01/2017.
 */
public class ListarDispositivosP extends ListActivity {
    public static String ENDERECO_MAC = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bluetooth bluetooth = new Bluetooth(getApplicationContext());
        Set<BluetoothDevice> listaDispositivo = bluetooth.dispositvosPareados();
        ArrayAdapter<String> dispositivosPareados = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        if(listaDispositivo.size() > 0){
            for(BluetoothDevice dispositivos :listaDispositivo ){
                String nameBT = dispositivos.getName();
                String macBT = dispositivos.getAddress();
                dispositivosPareados.add(nameBT+"\n"+macBT);
            }

        }
        setListAdapter(dispositivosPareados);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String informacaoGeral = ((TextView)v).getText().toString();

        String enderecoMac = informacaoGeral.substring(informacaoGeral.length()-17);
        Toast.makeText(getApplicationContext(),"Endereço Mac"+enderecoMac,Toast.LENGTH_LONG).show();

        Intent retornaMac = new Intent();
        retornaMac.putExtra(ENDERECO_MAC,enderecoMac);
        setResult(RESULT_OK,retornaMac);
        finish();


    }
}
