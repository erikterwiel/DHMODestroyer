package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Erik on 10/23/17.
 */

public class BTBondReceiver extends BroadcastReceiver {

    private final String TAG = "BTBondReceiver.java";

    // Monitors bond status
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive() called");
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int state = device.getBondState();
            switch (state) {
                case BluetoothDevice.BOND_BONDING:
                    Log.i(TAG, "Bluetooth bond is being created");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.i(TAG, "Bluetooth bond is bonded");
                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.i(TAG, "Bluetooth bond is not bonded");
                    break;
            }
        }
    }
}
