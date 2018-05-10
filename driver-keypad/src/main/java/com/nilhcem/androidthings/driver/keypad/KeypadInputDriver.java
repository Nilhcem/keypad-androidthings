package com.nilhcem.androidthings.driver.keypad;

import android.view.KeyEvent;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.input.InputDriver;
import com.google.android.things.userdriver.input.InputDriverEvent;

import java.io.IOException;

public class KeypadInputDriver implements AutoCloseable {
    private static final String DRIVER_NAME = "Keypad";

    private Keypad mDevice;
    private InputDriver mInputDriver;
    private final InputDriverEvent mInputEvent = new InputDriverEvent();
    private int[] mKeys;

    private Keypad.OnKeyEventListener mKeyEventListener = new Keypad.OnKeyEventListener() {
        @Override
        public void onKeyEvent(KeyEvent keyEvent) {
            mInputEvent.clear();
            mInputEvent.setKeyPressed(keyEvent.getKeyCode(), keyEvent.getAction() == KeyEvent.ACTION_DOWN);
            mInputDriver.emit(mInputEvent);
        }
    };

    public KeypadInputDriver(String[] rowPins, String[] colPins, int[][] keys) throws IOException {
        mDevice = new Keypad(rowPins, colPins, keys);
        mKeys = flattenArray(keys);
    }

    @Override
    public void close() throws IOException {
        unregister();
        try {
            mDevice.close();
        } finally {
            mDevice = null;
        }
    }

    public void register() {
        if (mDevice == null) {
            throw new IllegalStateException("cannot registered closed driver");
        }
        if (mInputDriver == null) {
            mInputDriver = new InputDriver.Builder()
                    .setName(DRIVER_NAME)
                    .setSupportedKeys(mKeys)
                    .build();

            mDevice.register(mKeyEventListener);

            UserDriverManager.getInstance().registerInputDriver(mInputDriver);
        }
    }

    /**
     * Unregister the driver from the framework.
     */
    public void unregister() {
        if (mDevice == null) {
            throw new IllegalStateException("cannot unregistered closed driver");
        }

        if (mInputDriver != null) {
            UserDriverManager.getInstance().unregisterInputDriver(mInputDriver);
            mInputDriver = null;
        }

        mDevice.unregister();
    }

    private int[] flattenArray(int[][] array) {
        int nbRows = array.length;
        int nbCols = array[0].length;

        int[] flatten = new int[nbRows * nbCols];
        for (int row = 0; row < nbRows; row++) {
            System.arraycopy(array[row], 0, flatten, row * nbCols, nbCols);
        }
        return flatten;
    }
}
