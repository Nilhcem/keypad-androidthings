package com.nilhcem.androidthings.driver.keypad;

import android.view.InputDevice;
import android.view.KeyEvent;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.input.InputDriver;

import java.io.IOException;

public class KeypadInputDriver implements AutoCloseable {
    private static final String DRIVER_NAME = "Keypad";
    private static final int DRIVER_VERSION = 1;

    private Keypad mDevice;
    private InputDriver mDriver;
    private int[] mKeys;

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
        if (mDriver == null) {
            mDriver = build(mDevice, mKeys);
            UserDriverManager.getInstance().registerInputDriver(mDriver);
        }
    }

    /**
     * Unregister the driver from the framework.
     */
    public void unregister() {
        if (mDevice == null) {
            throw new IllegalStateException("cannot unregistered closed driver");
        }

        if (mDriver != null) {
            UserDriverManager.getInstance().unregisterInputDriver(mDriver);
            mDriver = null;
        }

        mDevice.unregister();
    }

    static InputDriver build(Keypad keypad, int[] keys) {
        final InputDriver inputDriver = new InputDriver.Builder(InputDevice.SOURCE_CLASS_BUTTON)
                .setName(DRIVER_NAME)
                .setVersion(DRIVER_VERSION)
                .setKeys(keys)
                .build();
        keypad.register(new Keypad.OnKeyEventListener() {
            @Override
            public void onKeyEvent(KeyEvent keyEvent) {
                inputDriver.emit(new KeyEvent[]{keyEvent});
            }
        });
        return inputDriver;
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
