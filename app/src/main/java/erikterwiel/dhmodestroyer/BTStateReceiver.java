package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Erik on 10/23/2017.
 */

public class BTStateReceiver extends BroadcastReceiver {

    private final String TAG = "BTStateReceiver.java";

    // Starts BondActivity when Bluetooth is turned on
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive() called");

        SharedPreferences senderDatabase =
                context.getSharedPreferences("senderDatabase", Context.MODE_PRIVATE);

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
                    if (senderDatabase.getString("toOpen", null).equals("Bond")) {
                        Log.i(TAG, "Bluetooth enabled, launching BondActivity");
                        Intent bondIntent = new Intent(context, BondActivity.class);
                        context.startActivity(bondIntent);
                    } else if (senderDatabase.getString("toOpen", null).equals("Connect")) {
                        Log.i(TAG, "Bluetooth enabled, launching ConnectActivity");
                        Intent connectIntent = new Intent(context, ConnectActivity.class);
                        context.startActivity(connectIntent);
                    }
                    break;
            }
        }
    }
}
