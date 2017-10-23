package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {

    private final String TAG = "LaunchActivity.java";

    BluetoothAdapter mBluetoothAdapter;
    BTStateReceiver mBTStateReceiver;
    Button mPairButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mPairButton = (Button) findViewById(R.id.launch_pair_button);

        // Monitors Bluetooth state
        mBTStateReceiver = new BTStateReceiver();
        IntentFilter btStateMonitorIntent =
                new IntentFilter (BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTStateReceiver, btStateMonitorIntent);

        // Turns Bluetooth on and displays paired devices on click
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = mBluetoothAdapter.getState();
                if (mBluetoothAdapter.isEnabled() && state == mBluetoothAdapter.STATE_CONNECTED) {
                    Intent controllerIntent =
                            new Intent(LaunchActivity.this, ControllerActivity.class);
                    startActivity(controllerIntent);
                } else if (mBluetoothAdapter.isEnabled()) {
                    Intent pairIntent = new Intent(LaunchActivity.this, PairActivity.class);
                    startActivity(pairIntent);
                } else {
                    Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(btEnableIntent);
                }
            }
        });
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
