package com.nilhcem.androidthings.driver.keypad.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.nilhcem.androidthings.driver.keypad.Keypad;

import java.io.IOException;

public class SampleActivity extends Activity {

    private static final String[] ROW_PINS = new String[]{"BCM12", "BCM16", "BCM20", "BCM21"};
    private static final String[] COL_PINS = new String[]{"BCM25", "BCM24", "BCM23", "BCM27"};

    private static final String TAG = SampleActivity.class.getSimpleName();

    private Keypad mKeypad;

    private Keypad.OnKeyEventListener mListener = new Keypad.OnKeyEventListener() {
        @Override
        public void onKeyEvent(KeyEvent keyEvent) {
            String action = keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : "ACTION_UP";
            Log.i(TAG, "onKeyEvent: (" + action + "): " + keyEvent.getDisplayLabel());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mKeypad = new Keypad(ROW_PINS, COL_PINS, Keypad.KEYS_4x4);
            mKeypad.register(mListener);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing keypad", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mKeypad.unregister();
            mKeypad.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing keypad", e);
        }
    }
}
