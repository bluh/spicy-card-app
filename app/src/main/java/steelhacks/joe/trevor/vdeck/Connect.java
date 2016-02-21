package steelhacks.joe.trevor.vdeck;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

public class Connect extends AppCompatActivity {

    BluetoothAdapter bAdapt;
    BroadcastReceiver bFilter;
    BluetoothServerSocket serverSocket;
    ArrayList<BluetoothDevice> devices;
    int REQUEST_ENABLE_BT = 1;

    private void addToTable(String val, BluetoothDevice device){
        TableLayout tab = (TableLayout) findViewById(R.id.bluetoothTable);
        TableRow row = new TableRow(this);
        Button txt = new Button(this);
        txt.setText(val);
        txt.setOnClickListener(new View.OnClickListener() {
            Context context;
            BluetoothDevice device;

            public void onClick(View v) {
                AlertDialog confirm = (new AlertDialog.Builder(context))
                    .setTitle("Confirm")
                    .setMessage("Connect to " + device.getName() + "?")
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            bAdapt.cancelDiscovery();
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Log.w("CC", "Making socket");
                                        BluetoothSocket sock = device.createRfcommSocketToServiceRecord(UUID.fromString(getString(R.string.UUID)));
                                        sock.connect();
                                        Log.w("CC", "socket connected");
                                        BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                                        BufferedWriter write = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                                        Log.w("CC", "writing to sock");
                                        write.write(getString(R.string.UUID) + "\n");
                                        write.flush();
                                        Log.w("CC", "reading from sock");
                                        String line = read.readLine();
                                        Log.w("CC", "got: " + line);
                                        if(line.equals(getString(R.string.UUID))){
                                            startActivity(new Intent(context, WaitingRoom.class));
                                        }else {
                                            bAdapt.startDiscovery();
                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();
                confirm.show();
            }

            View.OnClickListener setArgs(Context context, BluetoothDevice device) {
                this.context = context;
                this.device = device;
                return this;
            }
        }.setArgs(this, device));
        row.addView(txt);
        tab.addView(row);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        devices = new ArrayList<>();
        TextView progress = (TextView) findViewById(R.id.progress);
        progress.setText("Initializing Bluetooth...");
        //initialize bluetooth
        bAdapt = BluetoothAdapter.getDefaultAdapter();
        if(bAdapt == null){
            //bluetooth not supported
            AlertDialog err = (new AlertDialog.Builder(this))
                    .setTitle("Error")
                    .setMessage("Your phone does not support bluetooth, which is required! Sorry!")
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            err.show();
            return;
        }
        progress.setText("Enabling Bluetooth...");
        if (!bAdapt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        progress.setText("Initializing Bluetooth Server...");
        try {
            serverSocket = bAdapt.listenUsingRfcommWithServiceRecord("VDeck", UUID.fromString(getString(R.string.UUID)));
            final ServerThread sockThread = new ServerThread(serverSocket, this);
            sockThread.addListener(new SocketListener() {
                Context context;

                public void SocketConnect(BluetoothSocket sock) {
                    //etc
                }

                public void SocketMessage(BluetoothSocket sock, String message) {
                    Log.w("SC","message: " + sock.toString() + " ='[ " + message);
                    //someone trying to connect to us
                    if(message.equals(getString(R.string.UUID))) {
                        sockThread.send(sock, getString(R.string.UUID));
                        startActivity(new Intent(context, Game.class));
                    }
                }

                public void SocketDisconnect(BluetoothSocket sock) {
                    //etc
                }

                public SocketListener setContext(Context c){
                    this.context = c;
                    return this;
                }
            }.setContext(this));
        } catch (IOException e) {
            ErrorMessageAlert.create(this, e.toString(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }
        progress.setText("Discovering Devices...");
        bAdapt.startDiscovery();
        bFilter = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if((device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE) && !devices.contains(device)) {
                        addToTable(device.getBluetoothClass().getDeviceClass() + ": " + device.getName() + " @ " + device.getAddress(), device);
                        devices.add(device);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bFilter, filter);
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_CANCELED){
                AlertDialog err = (new AlertDialog.Builder(this))
                    .setTitle("Error")
                    .setMessage("Your phone does not support bluetooth, which is required! Sorry!")
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
                err.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bFilter);
        if(bAdapt.isDiscovering()){
            bAdapt.cancelDiscovery();
        }
    }
}
