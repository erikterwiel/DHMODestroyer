package erikterwiel.dhmodestroyer

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BTBondReceiver : BroadcastReceiver() {

    private val TAG = "BTBondReceiver.kt"

    // Monitors bond status
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive() called")
        val action = intent.action
        if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val state = device.bondState
            when (state) {
                BluetoothDevice.BOND_BONDING -> Log.i(TAG, "Bluetooth bond is being created")
                BluetoothDevice.BOND_BONDED -> Log.i(TAG, "Bluetooth bond is bonded")
                BluetoothDevice.BOND_NONE -> Log.i(TAG, "Bluetooth bond is not bonded")
            }
        }
    }
}
