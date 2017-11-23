package erikterwiel.dhmodestroyer

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class LaunchActivity : AppCompatActivity() {

    private val TAG = "LaunchActivity.kt"

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBTStateReceiver: BTStateReceiver
    private lateinit var mBondButton: Button
    private lateinit var mConnectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        mBondButton = findViewById(R.id.launch_bond_button) as Button
        mConnectButton = findViewById(R.id.launch_connect_button) as Button

        // Registers database to store which button is asking for Bluetooth access
        val senderDatabase = getSharedPreferences("senderDatabase", Context.MODE_PRIVATE)
        val databaseEditor = senderDatabase.edit()

        // Turns Bluetooth on and displays available devices on click
        mBondButton.setOnClickListener {
            val state = mBluetoothAdapter.state
            if (mBluetoothAdapter.isEnabled && state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "Bluetooth enabled, launching ControllerActivity")
                val controllerIntent = Intent(this@LaunchActivity, ControllerActivity::class.java)
                startActivity(controllerIntent)
            } else if (mBluetoothAdapter.isEnabled) {
                Log.i(TAG, "Bluetooth enabled, launching BondActivity")
                val bondIntent = Intent(this@LaunchActivity, BondActivity::class.java)
                startActivity(bondIntent)
            } else {
                Log.i(TAG, "Bluetooth not enabled, requesting on")
                databaseEditor.putString("toOpen", "Bond")
                databaseEditor.apply()
                val btEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(btEnableIntent)
            }
        }

        // Connects to bonded devices
        mConnectButton.setOnClickListener {
            if (!mBluetoothAdapter.isEnabled) {
                Log.i(TAG, "Bluetooth not enabled, requesting on")
                databaseEditor.putString("toOpen", "Connect")
                databaseEditor.apply()
                val btEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(btEnableIntent)
            } else {
                Log.i(TAG, "Bluetooth enabled, launching ConnectActivity")
                val connectIntent = Intent(this@LaunchActivity, ConnectActivity::class.java)
                startActivity(connectIntent)
            }
        }
    }

    override fun onStart() {
        Log.i(TAG, "onStart() called")
        super.onStart()
        // Monitors Bluetooth state
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBTStateReceiver = BTStateReceiver()
        val btStateMonitorIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mBTStateReceiver, btStateMonitorIntent)
    }

    override fun onStop() {
        Log.i(TAG, "onStop() called")
        super.onStop()
        try {
            unregisterReceiver(mBTStateReceiver)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

    }
}
