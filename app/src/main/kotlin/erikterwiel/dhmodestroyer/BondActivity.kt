package erikterwiel.dhmodestroyer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import java.util.ArrayList

class BondActivity : AppCompatActivity() {

    private val TAG = "BondActivity.kt"
    private val ITAG = "BTDiscoverReceiver.kt"

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBTBondReceiver: BTBondReceiver? = null
    private var mBTDiscoverReceiver: BTDiscoverReceiver? = null
    private val mBTDevices = ArrayList<BluetoothDevice>()
    private var mDeviceList: RecyclerView? = null
    private var mDeviceAdapter: DeviceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair_connect)

        // Monitors Bluetooth device bond status
        mBTBondReceiver = BTBondReceiver()
        val btBondMonitorIntent = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mBTBondReceiver, btBondMonitorIntent)

        // Starts Bluetooth device discovery
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
            Log.i(TAG, "Bluetooth discovery is now stopped")
        }
        mBTDiscoverReceiver = BTDiscoverReceiver()
        checkBTPermissions()
        mBluetoothAdapter!!.startDiscovery()
        Log.i(TAG, "Bluetooth discovery is now running")
        val btDiscoverIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mBTDiscoverReceiver, btDiscoverIntent)

        // Displays discovered Bluetooth devices from ArrayList
        mDeviceList = findViewById(R.id.pair_device_list) as RecyclerView
        mDeviceList!!.layoutManager = LinearLayoutManager(this)
        mDeviceAdapter = DeviceAdapter(mBTDevices)
        mDeviceList!!.adapter = mDeviceAdapter
    }

    fun checkBTPermissions() {
        var permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
        if (permissionCheck != 0) {
            this.requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                    1001)
        }
    }

    override fun onStop() {
        Log.i(TAG, "onStop() called")
        super.onStop()
        try {
            unregisterReceiver(mBTBondReceiver)
            unregisterReceiver(mBTDiscoverReceiver)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

    }

    // Adds discovered Bluetooth devices to ArrayList
    private inner class BTDiscoverReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(ITAG, "onReceive() called")
            val action = intent.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                Log.i(TAG, "Bluetooth device discovered")
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mBTDevices.add(device)
                mDeviceAdapter!!.itemAdded(mBTDevices.size - 1)
            }
        }
    }

    // RecyclerView adapters and holders below
    private inner class DeviceAdapter(private val btDevices: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<DeviceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            val layoutInflater = LayoutInflater.from(this@BondActivity)
            val view = layoutInflater.inflate(R.layout.list_item_device, parent, false)
            return DeviceHolder(view)
        }

        override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
            val device = btDevices[position]
            holder.bindDevice(device)
        }

        override fun getItemCount(): Int {
            return btDevices.size
        }

        fun itemAdded(position: Int) {
            notifyItemInserted(position)
        }
    }

    private inner class DeviceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mDeviceLayout: LinearLayout
        private val mDeviceName: TextView
        private val mDeviceAddress: TextView

        init {
            mDeviceLayout = itemView.findViewById<View>(R.id.device_layout) as LinearLayout
            mDeviceName = itemView.findViewById<View>(R.id.device_name) as TextView
            mDeviceAddress = itemView.findViewById<View>(R.id.device_address) as TextView
        }

        fun bindDevice(device: BluetoothDevice) {
            mDeviceLayout.setOnClickListener {
                mBluetoothAdapter!!.cancelDiscovery()
                device.createBond()
                finish()
            }
            mDeviceName.text = device.name
            mDeviceAddress.text = device.address
        }
    }
}
