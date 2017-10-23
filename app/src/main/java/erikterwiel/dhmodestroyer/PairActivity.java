package erikterwiel.dhmodestroyer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PairActivity extends AppCompatActivity {

    private final String TAG = "PairActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);
    }
}
