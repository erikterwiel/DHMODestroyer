package erikterwiel.dhmodestroyer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.UUID;

public class ControllerActivity extends AppCompatActivity {

    private final String TAG = "ControllerActivity.java";
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        mDevice = getIntent().getParcelableExtra("Device");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        new ConnectBluetooth().execute();

        View decorView = getWindow().getDecorView();
        int uIOptions =
                  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uIOptions);

    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop() called");
        super.onStop();
        try {
            mBluetoothSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ConnectBluetooth extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                mBluetoothSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
                mBluetoothSocket.connect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
