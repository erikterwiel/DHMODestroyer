package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {

    private final String TAG = "LaunchActivity.java";

    BluetoothAdapter mBluetoothAdapter;
    BTStateReceiver mBTStateReceiver;
    Button mBondButton;
    Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mBondButton = (Button) findViewById(R.id.launch_bond_button);
        mConnectButton = (Button) findViewById(R.id.launch_connect_button);

        // Registers database to store which button is asking for Bluetooth access
        SharedPreferences senderDatabase =
                getSharedPreferences("senderDatabase", Context.MODE_PRIVATE);
        final SharedPreferences.Editor databaseEditor = senderDatabase.edit();

        // Turns Bluetooth on and displays available devices on click
        mBondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = mBluetoothAdapter.getState();
                if (mBluetoothAdapter.isEnabled() && state == mBluetoothAdapter.STATE_CONNECTED) {
                    Log.i(TAG, "Bluetooth enabled, launching ControllerActivity");
                    Intent controllerIntent =
                            new Intent(LaunchActivity.this, ControllerActivity.class);
                    startActivity(controllerIntent);
                } else if (mBluetoothAdapter.isEnabled()) {
                    Log.i(TAG, "Bluetooth enabled, launching BondActivity");
                    Intent bondIntent = new Intent(LaunchActivity.this, BondActivity.class);
                    startActivity(bondIntent);
                } else {
                    Log.i(TAG, "Bluetooth not enabled, requesting on");
                    databaseEditor.putString("toOpen", "Bond");
                    databaseEditor.apply();
                    Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(btEnableIntent);
                }
            }
        });

        // Connects to bonded devices
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Log.i(TAG, "Bluetooth not enabled, requesting on");
                    databaseEditor.putString("toOpen", "Connect");
                    databaseEditor.apply();
                    Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(btEnableIntent);
                } else {
                    Log.i(TAG, "Bluetooth enabled, launching ConnectActivity");
                    Intent connectIntent = new Intent(LaunchActivity.this, ConnectActivity.class);
                    startActivity(connectIntent);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart() called");
        super.onStart();
        // Monitors Bluetooth state
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTStateReceiver = new BTStateReceiver();
        IntentFilter btStateMonitorIntent =
                new IntentFilter (BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTStateReceiver, btStateMonitorIntent);
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop() called");
        super.onStop();
        try {
            unregisterReceiver(mBTStateReceiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
