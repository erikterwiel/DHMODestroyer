package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Erik on 10/23/2017.
 */

public class BTStateReceiver extends BroadcastReceiver {

    private final String TAG = "BTStateReceiver.java";

    // Starts PairActivity when Bluetooth is turned on
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive() called");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String action = intent.getAction();
        if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.i(TAG, "Bluetooth is turning off");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.i(TAG, "Bluetooth is now off");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i(TAG, "Bluetooth is turning on");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.i(TAG, "Bluetooth is now on");
                    Intent pairIntent = new Intent(context, PairActivity.class);
                    context.startActivity(pairIntent);
                    break;
            }
        }
    }
}
