# Matrix Keypad driver for Android Things

## Download

```groovy
dependencies {
    compile 'com.nilhcem.androidthings:driver-keypad:0.0.1'
}
```

## Usage

```java
String[] rowPins = new String[]{"BCM12", "BCM16", "BCM20", "BCM21"};
String[] colPins = new String[]{"BCM25", "BCM24", "BCM23", "BCM27"};
 
Keypad keypad = new Keypad(rowPins, colPins, Keypad.KEYS_4x4);
// For a 3x4 matrix, you can use the "Keypad.KEYS_3x4" constant. You can also set your own custom keys.
 
keypad.register(new Keypad.OnKeyEventListener() {
    @Override
    public void onKeyEvent(KeyEvent keyEvent) {
        String action = keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : "ACTION_UP";
        Log.i(TAG, "onKeyEvent: (" + action + "): " + keyEvent.getDisplayLabel());
    }
});
 
// Don't forget to:
keypad.unregister();
keypad.close();
```

Alternatively, you can register a `KeypadInputDriver` with the system and receive `KeyEvents` through the standard Android APIs:

```java
KeypadInputDriver mInputDriver;

try {
    mInputDriver = new KeypadInputDriver(rowPins, colPins, Keypad.KEYS_4x4);
    mInputDriver.register();
} catch (IOException e) {
    // error configuring keypad...
}

// Override key event callbacks in your Activity:

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    Log.i(TAG, "onKeyDown: " + event.getDisplayLabel());
    return true;
}

@Override
public boolean onKeyUp(int keyCode, KeyEvent event) {
    Log.i(TAG, "onKeyUp: " + event.getDisplayLabel());
    return true;
}

// Unregister and close the input driver when finished:

mInputDriver.unregister();
try {
    mInputDriver.close();
} catch (IOException e) {
    // error closing input driver
}
```

Also, don't forget to add the required permission to your app's manifest file:

```xml
<uses-permission android:name="com.google.android.things.permission.MANAGE_INPUT_DRIVERS" />
```

### Schematic

The sample is for a 4x4 Membrane Matrix Keypad, but the library should work for any keypad formats (e.g. 3x4).  
We use 1k pull-up resistors for the rows

![schematic][]

![rowscols][]

* Row 1 -> BCM12
* Row 2 -> BCM16
* Row 3 -> BCM20
* Row 4 -> BCM21
* Col 1 -> BCM25
* Col 2 -> BCM24
* Col 3 -> BCM23
* Col 4 -> BCM27

## Kudos to

* [Polidea][polidea] for the Polithings numpad12 driver.
* [ciromattia][ciromattia] for the Fritzing 4x4 membrane matrix keypad.

[rowscols]: https://raw.githubusercontent.com/Nilhcem/keypad-androidthings/master/assets/rowscols.png
[schematic]: https://raw.githubusercontent.com/Nilhcem/keypad-androidthings/master/assets/schematic.png

[polidea]: https://github.com/Polidea/Polithings/tree/master/numpad
[ciromattia]: https://github.com/ciromattia/Fritzing-Library
