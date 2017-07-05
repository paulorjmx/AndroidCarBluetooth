package com.example.epc.bluetoothapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.bluetooth.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter local_adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket _client = null;
    private InputStream in = null;
    private OutputStream out = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter changestate_filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter scan_started = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter scan_finished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter device_found = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetooth_broadreceiver, changestate_filter);
        registerReceiver(bluetooth_broadreceiver, scan_started);
        registerReceiver(bluetooth_broadreceiver, scan_finished);
        registerReceiver(bluetooth_broadreceiver, device_found);

    }

    // BROADCAST RECEIVER FOR CHANGE STATE OF BLUETOOTH
    public final BroadcastReceiver bluetooth_broadreceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Context app_context = getApplicationContext();
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON)
                {
                    Toast.makeText(app_context, "Bluetooth ativado!", Toast.LENGTH_SHORT).show();
                }
                /*else
                {
                    Toast.makeText(app_context, "Ative seu bluetooth!", Toast.LENGTH_SHORT).show();
                }*/
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Toast.makeText(app_context, "DISCOVERY STARTED!", Toast.LENGTH_SHORT).show();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Toast.makeText(app_context, "DISCOVERY FINISHED!", Toast.LENGTH_SHORT).show();
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                Toast.makeText(app_context, "DEVICE FOUND!", Toast.LENGTH_SHORT).show();
                BluetoothDevice dev_found = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String device_name = dev_found.getName();
                //Toast.makeText(app_context, device_name, Toast.LENGTH_SHORT).show();
                if(device_name.toLowerCase().equals("hc-05"))
                {
                    Toast.makeText(app_context, "HC FOUND!", Toast.LENGTH_SHORT).show();
                    //String device_addr = dev_found.getAddress();
                    try
                    {
                        _client = dev_found.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        _client.connect();
                        if(_client.isConnected())
                        {
                            Toast.makeText(app_context, "Connected!", Toast.LENGTH_SHORT).show();
                            manageSocketConnection(_client);
                        }
                        else
                        {
                            Toast.makeText(app_context, "Fail on Connected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(IOException e)
                    {
                        Toast.makeText(app_context, "BT_SERVER failed: " +e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(app_context, "HC NOT FOUND!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void manageSocketConnection(BluetoothSocket acc_socket)
    {
        try
        {
            in = acc_socket.getInputStream();
            out = acc_socket.getOutputStream();
        }
        catch(IOException e)
        {
            Toast.makeText(this, "EXCEPTION ON GET IN OUT STREAMS: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btn_upclick(View v)
    {
        byte[] buffer = new byte[1];
        buffer = "w".getBytes();
        try
        {
            out.flush();
            out.write(buffer);
        }
        catch (IOException e)
        {
            Toast.makeText(this, "WRITE EXCEPTION: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btn_leftclick(View v)
    {
        byte[] buffer = new byte[1];
        buffer = "a".getBytes();
        try
        {
            out.flush();
            out.write(buffer);
        }
        catch (IOException e)
        {
            Toast.makeText(this, "WRITE EXCEPTION: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btn_rightclick(View v)
    {
        byte[] buffer = new byte[1];
        buffer = "d".getBytes();
        try
        {
            out.flush();
            out.write(buffer);
        }
        catch (IOException e)
        {
            Toast.makeText(this, "WRITE EXCEPTION: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btn_downclick(View v)
    {
        byte[] buffer = new byte[1];
        buffer = "s".getBytes();
        try
        {
            out.flush();
            out.write(buffer);
        }
        catch (IOException e)
        {
            Toast.makeText(this, "WRITE EXCEPTION: " +e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void turnon_click(View v)
    {
        if(local_adapter == null)
        {
            AlertDialog.Builder not_support = new AlertDialog.Builder(this);
            not_support.setTitle("Alerta");
            not_support.setMessage("Seu dispositivo não tem um adapatador bluetooth!");
            not_support.setPositiveButton("OK",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1)
                        {
                            finish();
                        }
                    });
            not_support.show();
        }
        else
        {
            if(local_adapter.isEnabled() != true)
            {
                Intent enable_bt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enable_bt, REQUEST_ENABLE_BT);
                return;
            }
        }
    }

    public void scan_click(View v)
    {
        if(local_adapter.isEnabled() == true)
        {
            local_adapter.startDiscovery();
        }
        else
        {
            Toast.makeText(this, "TURN ON YOUR BLUETOOTH ADAPTER!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent data)
    {
        if(request_code == REQUEST_ENABLE_BT)
        {
            if(result_code ==  RESULT_OK)
            {
                if(local_adapter.startDiscovery() != true)
                {
                    Toast.makeText(this, "DISCOVERY START", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            if(_client != null)
            {
                if(_client.isConnected())
                {
                    _client.close();
                }
            }
            if(out != null)
            {
                out.flush();
                out.close();
            }
            if(in != null)
            {
                in.close();
            }
        }
        catch(IOException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        unregisterReceiver(bluetooth_broadreceiver);
    }
}
