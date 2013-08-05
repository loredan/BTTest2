package com.example.BTTest2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: loredan
 * Date: 05.08.13
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class ExpectConnectThread extends Thread
{
    MyActivity activity;
    BluetoothAdapter adapter;


    public ExpectConnectThread(BluetoothAdapter btAdapter, MyActivity _activity)
    {
        Log.i("ExpectConnectThread", "started");
        adapter = btAdapter;
        activity = _activity;
        try
        {
            Ref.btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("BTTest2", Ref.uuid);
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            Log.i("Bluetooth", "Try");
            try
            {
                Ref.btSocket = Ref.btServerSocket.accept();
            } catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Log.i("Bluetooth", "Fail");
            if(Ref.btSocket!=null)
            {
                Log.i("Bluetooth", "Connection detected");
                try
                {
                    Ref.btServerSocket.close();
                } catch (IOException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
//                adapter.cancelDiscovery();
                Intent connectIntent = new Intent(activity, ChatActivity.class);
                connectIntent.putExtra("server", true);
                activity.startActivity(connectIntent);
                break;
            }
        }
    }
}
