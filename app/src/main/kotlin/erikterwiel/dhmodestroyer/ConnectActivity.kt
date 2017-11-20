package erikterwiel.dhmodestroyer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
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

class ConnectActivity : AppCompatActivity() {

    private val TAG = "ConnectActivity.kt"

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mBondedDevices = ArrayList<BluetoothDevice>()
    private var mBondedList: RecyclerView? = null
    private var mDeviceAdapter: DeviceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair_connect)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bondedDeviceSet = mBluetoothAdapter!!.bondedDevices
        for (device in bondedDeviceSet) mBondedDevices.add(device)

        mBondedList = findViewById(R.id.pair_device_list) as RecyclerView
        mBondedList!!.layoutManager = LinearLayoutManager(this)
        mDeviceAdapter = DeviceAdapter(mBondedDevices)
        mBondedList!!.adapter = mDeviceAdapter
    }

    // RecyclerView adapters and holders below
    private inner class DeviceAdapter(private val bondedDevices: ArrayList<BluetoothDevice>) :
            RecyclerView.Adapter<DeviceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            val layoutInflater = LayoutInflater.from(this@ConnectActivity)
            val view = layoutInflater.inflate(R.layout.list_item_device, parent, false)
            return DeviceHolder(view)
        }

        override fun onBindViewHolder(holder: ConnectActivity.DeviceHolder, position: Int) {
            val device = bondedDevices[position]
            holder.bindDevice(device)
        }

        override fun getItemCount(): Int {
            return bondedDevices.size
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
                val controllerIntent = Intent(this@ConnectActivity, ControllerActivity::class.java)
                controllerIntent.putExtra("Device", device)
                startActivity(controllerIntent)
            }
            mDeviceName.text = device.name
            mDeviceAddress.text = device.address
        }
    }
}
