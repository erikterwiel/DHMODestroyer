package erikterwiel.dhmodestroyer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PairActivity extends AppCompatActivity {

    private final String TAG = "PairActivity.java";
    private final String ITAG = "BTDiscoverReceiver.java";

    private BluetoothAdapter mBluetoothAdapter;
    private BTDiscoverReceiver mBTDiscoverReceiver;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private RecyclerView mDeviceList;
    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        // Starts Bluetooth device discovery
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.i(TAG, "Bluetooth discovery is now stopped");
        }
        mBTDiscoverReceiver = new BTDiscoverReceiver();
        checkBTPermissions();
        mBluetoothAdapter.startDiscovery();
        Log.i(TAG, "Bluetooth discovery is now running");
        IntentFilter btDiscoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTDiscoverReceiver, btDiscoverIntent);

        // Displays discovered Bluetooth devices from ArrayList
        mDeviceList = (RecyclerView) findViewById(R.id.pair_device_list);
        mDeviceList.setLayoutManager(new LinearLayoutManager(this));
        mDeviceAdapter = new DeviceAdapter(mBTDevices);
        mDeviceList.setAdapter(mDeviceAdapter);
    }

    public void checkBTPermissions() {
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1001);
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop() called");
        super.onStop();
        unregisterReceiver(mBTDiscoverReceiver);
    }

    // Adds discovered Bluetooth devices to ArrayList
    private class BTDiscoverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(ITAG, "onReceive() called");
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.i(TAG, "Bluetooth device discovered");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                mDeviceAdapter.itemAdded(device, mBTDevices.size() - 1);
            }
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
        private ArrayList<BluetoothDevice> btDevices;

        public DeviceAdapter(ArrayList<BluetoothDevice> passedBTDevices) {
            btDevices = passedBTDevices;
        }

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(PairActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_device, parent, false);
            return new PairActivity.DeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(DeviceHolder holder, int position) {
            BluetoothDevice device = btDevices.get(position);
            holder.bindDevice(device);
        }

        @Override
        public int getItemCount() {
            return btDevices.size();
        }

        public void itemAdded(BluetoothDevice device, int position) {
            notifyItemInserted(position);
        }
    }

    private class DeviceHolder extends RecyclerView.ViewHolder {

        private TextView mDeviceName;
        private TextView mDeviceAddress;

        public DeviceHolder(View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView.findViewById(R.id.device_name);
            mDeviceAddress = (TextView) itemView.findViewById(R.id.device_address);
        }

        public void bindDevice(BluetoothDevice device) {
            mDeviceName.setText(device.getName());
            mDeviceAddress.setText(device.getAddress());
        }
    }
}
