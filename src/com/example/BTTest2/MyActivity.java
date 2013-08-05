package com.example.BTTest2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MyActivity extends Activity
{
    public BluetoothAdapter btAdapter;
    public ArrayList<BluetoothDevice> devices;
    public ArrayAdapter<String> btArrAdapter;
    public static final int REQUEST_ENABLE_BT = 1;
    public LinearLayout llDevices;
    public ListView lvDevices;
    public BroadcastReceiver btDeviceFound;
    public ExpectConnectThread expectConnectThread;
    public Handler handler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Ref.init();
        setContentView(R.layout.main);

        llDevices = (LinearLayout) findViewById(R.id.ll_devices);
        lvDevices = (ListView) findViewById(R.id.lv_devices);
        btArrAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1);
        lvDevices.setAdapter(btArrAdapter);

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.i("Item", "Click");
                String name = (String) ((TextView) view).getText();
                BluetoothDevice selectedDevice = null;
                BluetoothSocket temp = null;
                for (BluetoothDevice device : devices)
                {
                    String devName = device.getName();
                    if (device.getName().equals(name))
                    {
                        selectedDevice = device;
                        break;
                    }
                }
//                btAdapter.cancelDiscovery();
                try
                {
                    temp = selectedDevice.createRfcommSocketToServiceRecord(Ref.uuid);
                } catch (IOException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Ref.btSocket = temp;
                try
                {
                    Ref.btSocket.connect();
                } catch (IOException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Intent connectIntent = new Intent(getApplicationContext(), ChatActivity.class);
                connectIntent.putExtra("server", false);
                connectIntent.putExtra("device", selectedDevice);
                startActivity(connectIntent);
            }
        });

        Log.i("Bluetooth", "Fetch adapter");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled())
        {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        }
        Log.i("Bluetooth", "Adapter locked, start server connection thread");
        expectConnectThread = new ExpectConnectThread(btAdapter, this);
        expectConnectThread.start();
        Log.i("Bluetooth", "Thread started, device search");
        devices = new ArrayList<BluetoothDevice>(btAdapter.getBondedDevices());
        if (devices.size() > 0)
        {
            for (BluetoothDevice device : devices)
            {
//                devices.add(device);
                btArrAdapter.add(device.getName());
//                lvDevices.addView(tvDevice);
//                lvDevices.addView(tvDevice);
            }
        }

        btDeviceFound = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("foundDevice", foundDevice == null ? "null" : "OK");
//                    Ref.devices.add(foundDevice);
                    TextView tvDevice = new TextView(getApplicationContext());
                    tvDevice.setText((CharSequence) foundDevice.getName());
                    devices.add(foundDevice);
                    btArrAdapter.add(foundDevice.getName());
//                    lvDevices.addView(tvDevice);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btDeviceFound, filter);

//        btAdapter.startDiscovery();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(btDeviceFound);
    }
}
