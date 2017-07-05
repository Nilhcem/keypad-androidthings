package com.nilhcem.androidthings.driver.keypad;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.KeyEvent.*;

public class Keypad implements AutoCloseable {

    public static final int[][] KEYS_4x4 = new int[][]{
            new int[]{KEYCODE_1, KEYCODE_2, KEYCODE_3, KEYCODE_A},
            new int[]{KEYCODE_4, KEYCODE_5, KEYCODE_6, KEYCODE_B},
            new int[]{KEYCODE_7, KEYCODE_8, KEYCODE_9, KEYCODE_C},
            new int[]{KEYCODE_STAR, KEYCODE_0, KEYCODE_POUND, KEYCODE_D},
    };

    public static final int[][] KEYS_4x3 = new int[][]{
            new int[]{KEYCODE_1, KEYCODE_2, KEYCODE_3},
            new int[]{KEYCODE_4, KEYCODE_5, KEYCODE_6},
            new int[]{KEYCODE_7, KEYCODE_8, KEYCODE_9},
            new int[]{KEYCODE_STAR, KEYCODE_0, KEYCODE_POUND},
    };

    private static final String TAG = Keypad.class.getSimpleName();
    private static final long THREAD_SLEEP_DELAY_MS = 30;

    public interface OnKeyEventListener {
        void onKeyEvent(KeyEvent keyEvent);
    }

    private OnKeyEventListener mOnKeyEventListener;
    private final Gpio[] mRowGpios;
    private final Gpio[] mColGpios;
    private final int[][] mKeys;

    private int mLastKey = KEYCODE_UNKNOWN;

    private AtomicBoolean mIsActive = new AtomicBoolean(false);

    public Keypad(String[] rowPins, String[] colPins, int[][] keys) throws IOException {
        PeripheralManagerService pioService = new PeripheralManagerService();
        mRowGpios = new Gpio[rowPins.length];
        mColGpios = new Gpio[rowPins.length];

        for (int i = 0; i < rowPins.length; i++) {
            mRowGpios[i] = pioService.openGpio(rowPins[i]);
            mRowGpios[i].setDirection(Gpio.DIRECTION_IN);
            mRowGpios[i].setEdgeTriggerType(Gpio.EDGE_BOTH);
            mRowGpios[i].setActiveType(Gpio.ACTIVE_LOW);
        }
        for (int i = 0; i < colPins.length; i++) {
            mColGpios[i] = pioService.openGpio(colPins[i]);
            mColGpios[i].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mColGpios[i].setValue(true);
        }

        mKeys = keys;
    }

    @Override
    public void close() throws IOException {
        for (int i = 0; i < mRowGpios.length; i++) {
            mRowGpios[i].close();
            mRowGpios[i] = null;
        }

        for (int i = 0; i < mColGpios.length; i++) {
            mColGpios[i].close();
            mRowGpios[i] = null;
        }
    }

    public void register(OnKeyEventListener onKeyEventListener) {
        mOnKeyEventListener = onKeyEventListener;
        mIsActive.set(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsActive.get()) {
                    int pressedKey = checkPressedKey();

                    if (mLastKey == KEYCODE_UNKNOWN && pressedKey != KEYCODE_UNKNOWN) {
                        notifyListener(pressedKey, ACTION_DOWN);
                        mLastKey = pressedKey;
                    }
                    if (mLastKey != KEYCODE_UNKNOWN && pressedKey == KEYCODE_UNKNOWN) {
                        notifyListener(mLastKey, ACTION_UP);
                        mLastKey = KEYCODE_UNKNOWN;
                    }

                    try {
                        Thread.sleep(THREAD_SLEEP_DELAY_MS);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Sleep error");
                    }
                }
            }
        }).start();
    }

    public void unregister() {
        mIsActive.set(false);
        mOnKeyEventListener = null;
    }

    private void notifyListener(final int key, final int action) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mOnKeyEventListener.onKeyEvent(new KeyEvent(action, key));
            }
        });
    }

    private int checkPressedKey() {
        int key = KEYCODE_UNKNOWN;
        Gpio colGpio;

        try {
            for (int col = 0; key == KEYCODE_UNKNOWN && col < mColGpios.length; col++) {
                colGpio = mColGpios[col];

                colGpio.setValue(false);
                for (int row = 0; row < mRowGpios.length; row++) {
                    if (mRowGpios[row].getValue()) {
                        key = mKeys[row][col];
                        break;
                    }
                }
                colGpio.setValue(true);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error checking pressed key", e);
        }

        return key;
    }
}
