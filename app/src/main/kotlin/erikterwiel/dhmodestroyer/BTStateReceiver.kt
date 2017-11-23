package erikterwiel.dhmodestroyer

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BTStateReceiver : BroadcastReceiver() {

    private val TAG = "BTStateReceiver.kt"

    // Starts BondActivity when Bluetooth is turned on
    override fun onReceive(context: Context, intent: Intent) {

        Log.i(TAG, "onReceive() called")

        val senderDatabase = context.getSharedPreferences("senderDatabase", Context.MODE_PRIVATE)

        val action = intent.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_TURNING_OFF -> Log.i(TAG, "Bluetooth is turning off")
                BluetoothAdapter.STATE_OFF -> Log.i(TAG, "Bluetooth is now off")
                BluetoothAdapter.STATE_TURNING_ON -> Log.i(TAG, "Bluetooth is turning on")
                BluetoothAdapter.STATE_ON -> {
                    Log.i(TAG, "Bluetooth is now on")
                    if (senderDatabase.getString("toOpen", null) == "Bond") {
                        Log.i(TAG, "Bluetooth enabled, launching BondActivity")
                        val bondIntent = Intent(context, BondActivity::class.java)
                        context.startActivity(bondIntent)
                    } else if (senderDatabase.getString("toOpen", null) == "Connect") {
                        Log.i(TAG, "Bluetooth enabled, launching ConnectActivity")
                        val connectIntent = Intent(context, ConnectActivity::class.java)
                        context.startActivity(connectIntent)
                    }
                }
            }
        }
    }
}
