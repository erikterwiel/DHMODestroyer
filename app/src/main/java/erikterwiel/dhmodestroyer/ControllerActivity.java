package erikterwiel.dhmodestroyer;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class ControllerActivity extends AppCompatActivity {

    private final String TAG = "ControllerActivity.java";
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private SeekBar mVerticalStick;
    private SeekBar mHorizontalStick;
    private TextView mVerticalValue;
    private TextView mHorizontalValue;
    private ImageView mBluetoothStatus;
    private boolean mConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        mDevice = getIntent().getParcelableExtra("Device");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        new ConnectBluetooth().execute();

        View decorView = getWindow().getDecorView();
        int uIOptions =   View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uIOptions);
        setContentView(R.layout.activity_controller);

        mVerticalStick = (SeekBar) findViewById(R.id.controller_vertical_stick);
        mHorizontalStick = (SeekBar) findViewById(R.id.controller_horizontal_stick);
        mVerticalValue = (TextView) findViewById(R.id.controller_vertical_value);
        mHorizontalValue = (TextView) findViewById(R.id.controller_horizontal_value);
        mBluetoothStatus = (ImageView) findViewById(R.id.controller_status);

        mVerticalStick.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mBluetoothSocket.isConnected() && !mConnected) {
                    mBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_connected_white_48dp);
                    mConnected = true;
                } else if (!mBluetoothSocket.isConnected() && mConnected) {
                    mBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp);
                    mConnected = false;
                }
                mVerticalValue.setText(Integer.toString(progress - 50));
                try {
                    if (progress % 5 == 0) {
                        String toSend;
                        if (progress < 10) {
                            toSend = "v0" + Integer.toString(progress);
                        } else if (progress == 100) {
                            toSend = "v99";
                        } else {
                            toSend = "v" + Integer.toString(progress);
                        }
                        Log.i(TAG, "Sending " + toSend + " to DHMO Destroyer");
                        mBluetoothSocket.getOutputStream().write(toSend.getBytes());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                animate(seekBar, 50, 250);
            }
        });

        mHorizontalStick.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mBluetoothSocket.isConnected() && !mConnected) {
                    mBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_connected_white_48dp);
                    mConnected = true;
                } else if (!mBluetoothSocket.isConnected() && mConnected) {
                    mBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp);
                    mConnected = false;
                }
                mHorizontalValue.setText(Integer.toString(progress - 50));
                try {
                    if (progress % 5 == 0) {
                        String toSend;
                        if (progress < 10) {
                            toSend = "h0" + Integer.toString(progress);
                        } else if (progress == 100){
                            toSend = "h99";
                        }
                        else {
                            toSend = "h" + Integer.toString(progress);
                        }
                        Log.i(TAG, "Sending " + toSend + " to DHMO Destroyer");
                        mBluetoothSocket.getOutputStream().write(toSend.getBytes());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                animate(seekBar, 50, 250);
            }
        });

        // Changes SeekBar joystick drawables
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());

        BitmapDrawable thumbBitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(
                this, R.drawable.ic_swap_vertical_circle_white_48dp);
        Bitmap thumbBitmap = thumbBitmapDrawable.getBitmap();
        Drawable thumbDrawable = new BitmapDrawable(
                getResources(), Bitmap.createScaledBitmap(thumbBitmap, px, px, true));
        mVerticalStick.setThumb(thumbDrawable);

        BitmapDrawable thumbBitmapDrawable2 = (BitmapDrawable) ContextCompat.getDrawable(
                this, R.drawable.ic_swap_vertical_circle_white_48dp);
        Bitmap thumbBitmap2 = thumbBitmapDrawable2.getBitmap();
        Drawable thumbDrawable2 = new BitmapDrawable(
                getResources(), Bitmap.createScaledBitmap(thumbBitmap2, px, px, true));
        mHorizontalStick.setThumb(thumbDrawable2);

        mBluetoothStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothSocket.isConnected()) {
                    try {
                        mBluetoothSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    mConnected = false;
                    mBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp);
                } else {
                    new ConnectBluetooth().execute();
                }
            }
        });
    }

    private void animate(SeekBar seekBar, int progress, int speed) {
        ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", progress);
        animation.setDuration(speed);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
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
