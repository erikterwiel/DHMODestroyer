package erikterwiel.dhmodestroyer

import android.animation.ObjectAnimator
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

import org.w3c.dom.Text

import java.io.IOException
import java.util.UUID

class ControllerActivity : AppCompatActivity() {

    private val TAG = "ControllerActivity.kt"
    private val mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var mDevice: BluetoothDevice? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    private var mVerticalStick: SeekBar? = null
    private var mHorizontalStick: SeekBar? = null
    private var mVerticalValue: TextView? = null
    private var mHorizontalValue: TextView? = null
    private var mBluetoothStatus: ImageView? = null
    private var mConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)

        mDevice = intent.getParcelableExtra("Device")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        ConnectBluetooth().execute()

        val decorView = window.decorView
        val uIOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uIOptions
        setContentView(R.layout.activity_controller)

        mVerticalStick = findViewById(R.id.controller_vertical_stick) as SeekBar
        mHorizontalStick = findViewById(R.id.controller_horizontal_stick) as SeekBar
        mVerticalValue = findViewById(R.id.controller_vertical_value) as TextView
        mHorizontalValue = findViewById(R.id.controller_horizontal_value) as TextView
        mBluetoothStatus = findViewById(R.id.controller_status) as ImageView

        mVerticalStick!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mBluetoothSocket!!.isConnected && !mConnected) {
                    mBluetoothStatus!!.setImageResource(R.drawable.ic_bluetooth_connected_white_48dp)
                    mConnected = true
                } else if (!mBluetoothSocket!!.isConnected && mConnected) {
                    mBluetoothStatus!!.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp)
                    mConnected = false
                }
                mVerticalValue!!.text = Integer.toString(progress - 50)
                try {
                    if (progress % 5 == 0) {
                        val toSend: String
                        if (progress < 10) {
                            toSend = "v0" + Integer.toString(progress)
                        } else if (progress == 100) {
                            toSend = "v99"
                        } else {
                            toSend = "v" + Integer.toString(progress)
                        }
                        Log.i(TAG, "Sending $toSend to DHMO Destroyer")
                        mBluetoothSocket!!.outputStream.write(toSend.toByteArray())
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                animate(seekBar, 50, 250)
            }
        })

        mHorizontalStick!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mBluetoothSocket!!.isConnected && !mConnected) {
                    mBluetoothStatus!!.setImageResource(R.drawable.ic_bluetooth_connected_white_48dp)
                    mConnected = true
                } else if (!mBluetoothSocket!!.isConnected && mConnected) {
                    mBluetoothStatus!!.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp)
                    mConnected = false
                }
                mHorizontalValue!!.text = Integer.toString(progress - 50)
                try {
                    if (progress % 5 == 0) {
                        val toSend: String
                        if (progress < 10) {
                            toSend = "h0" + Integer.toString(progress)
                        } else if (progress == 100) {
                            toSend = "h99"
                        } else {
                            toSend = "h" + Integer.toString(progress)
                        }
                        Log.i(TAG, "Sending $toSend to DHMO Destroyer")
                        mBluetoothSocket!!.outputStream.write(toSend.toByteArray())
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                animate(seekBar, 50, 250)
            }
        })

        // Changes SeekBar joystick drawables
        val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics).toInt()

        val thumbBitmapDrawable = ContextCompat.getDrawable(
                this, R.drawable.ic_swap_vertical_circle_white_48dp) as BitmapDrawable
        val thumbBitmap = thumbBitmapDrawable.bitmap
        val thumbDrawable = BitmapDrawable(
                resources, Bitmap.createScaledBitmap(thumbBitmap, px, px, true))
        mVerticalStick!!.thumb = thumbDrawable

        val thumbBitmapDrawable2 = ContextCompat.getDrawable(
                this, R.drawable.ic_swap_vertical_circle_white_48dp) as BitmapDrawable
        val thumbBitmap2 = thumbBitmapDrawable2.bitmap
        val thumbDrawable2 = BitmapDrawable(
                resources, Bitmap.createScaledBitmap(thumbBitmap2, px, px, true))
        mHorizontalStick!!.thumb = thumbDrawable2

        mBluetoothStatus!!.setOnClickListener {
            if (mBluetoothSocket!!.isConnected) {
                try {
                    mBluetoothSocket!!.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                mConnected = false
                mBluetoothStatus!!.setImageResource(R.drawable.ic_bluetooth_searching_white_48dp)
            } else {
                ConnectBluetooth().execute()
            }
        }
    }

    private fun animate(seekBar: SeekBar, progress: Int, speed: Int) {
        val animation = ObjectAnimator.ofInt(seekBar, "progress", progress)
        animation.duration = speed.toLong()
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    override fun onStop() {
        Log.i(TAG, "onStop() called")
        super.onStop()
        try {
            mBluetoothSocket!!.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    private inner class ConnectBluetooth : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg devices: Void): Void? {
            try {
                mBluetoothSocket = mDevice!!.createInsecureRfcommSocketToServiceRecord(mUUID)
                mBluetoothSocket!!.connect()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            return null
        }
    }
}
