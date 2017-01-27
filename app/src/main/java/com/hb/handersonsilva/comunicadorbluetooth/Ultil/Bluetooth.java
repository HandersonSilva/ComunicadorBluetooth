package com.hb.handersonsilva.comunicadorbluetooth.Ultil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.hb.handersonsilva.comunicadorbluetooth.MainActivity;

import java.util.Set;

/**
 * Created by Handerson Silva on 24/01/2017.
 */
public class Bluetooth {
    BluetoothAdapter mBluetoothAdapter= null;

    Context context;


    public Bluetooth(Context ctx){
        context = ctx;
    }

    //Verificar se o aparelho suporta bluetooth
    public BluetoothAdapter verificarBluetooth(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(context,"Seu dispositivo não possui bluetooth",Toast.LENGTH_SHORT).show();
        }else if(!mBluetoothAdapter.isEnabled()){

            return mBluetoothAdapter;

           // Toast.makeText(context,"Bluetooth está ativado",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public Set<BluetoothDevice> dispositvosPareados(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> listDispositivos = mBluetoothAdapter.getBondedDevices();

       return listDispositivos;

    }

}
