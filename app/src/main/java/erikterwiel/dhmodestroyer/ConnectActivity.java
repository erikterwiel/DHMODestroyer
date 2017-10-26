package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class ConnectActivity extends AppCompatActivity {

    private final String TAG = "ConnectActivity.java";

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mBondedDevices = new ArrayList<BluetoothDevice>();
    private RecyclerView mBondedList;
    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_connect);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDeviceSet = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDeviceSet) mBondedDevices.add(device);

        mBondedList = (RecyclerView) findViewById(R.id.pair_device_list);
        mBondedList.setLayoutManager(new LinearLayoutManager(this));
        mDeviceAdapter = new DeviceAdapter(mBondedDevices);
        mBondedList.setAdapter(mDeviceAdapter);
    }

    // RecyclerView adapters and holders below
    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
        private ArrayList<BluetoothDevice> bondedDevices;

        public DeviceAdapter(ArrayList<BluetoothDevice> passedBTDevices) {
            bondedDevices = passedBTDevices;
        }

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ConnectActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_device, parent, false);
            return new DeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(ConnectActivity.DeviceHolder holder, int position) {
            BluetoothDevice device = bondedDevices.get(position);
            holder.bindDevice(device);
        }

        @Override
        public int getItemCount() {
            return bondedDevices.size();
        }
    }

    private class DeviceHolder extends RecyclerView.ViewHolder {

        private LinearLayout mDeviceLayout;
        private TextView mDeviceName;
        private TextView mDeviceAddress;

        public DeviceHolder(View itemView) {
            super(itemView);
            mDeviceLayout = (LinearLayout) itemView.findViewById(R.id.device_layout);
            mDeviceName = (TextView) itemView.findViewById(R.id.device_name);
            mDeviceAddress = (TextView) itemView.findViewById(R.id.device_address);
        }

        public void bindDevice(final BluetoothDevice device) {
            mDeviceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent controllerIntent = new Intent(ConnectActivity.this, ControllerActivity.class);
                    controllerIntent.putExtra("Device", device);
                    startActivity(controllerIntent);
                }
            });
            mDeviceName.setText(device.getName());
            mDeviceAddress.setText(device.getAddress());
        }
    }
}
