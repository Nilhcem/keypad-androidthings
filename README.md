# Matrix Keypad driver for Android Things

![photo][]

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
 
Keypad keypad = new Keypad(ROW_PINS, COL_PINS, Keypad.KEYS_4x4);
// For a 3x4 matrix, You can use the "Keypad.KEYS_3x4" constant. You can also set your own custom keys.
 
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


### Schematic

The sample is for a 4x4 Membrane Matrix Keypad, but the library should work for any keypad formats (e.g. 3x4).  
We use 1k pull-up resistors for the rows

![rowscols][]

![schematic][]

* Row 1 -> BCM12
* Row 2 -> BCM16
* Row 3 -> BCM20
* Row 4 -> BCM21
* Col 1 -> BCM25
* Col 2 -> BCM24
* Col 3 -> BCM23
* Col 4 -> BCM27

## Kudos to

[Polidea][polidea] for the Polithings numpad driver.
[ciromattia][ciromattia] for the Fritzing 4x4 membrane matrix keypad.

[rowscols]: https://raw.githubusercontent.com/Nilhcem/keypad-androidthings/master/assets/rowscols.png
[schematic]: https://raw.githubusercontent.com/Nilhcem/keypad-androidthings/master/assets/schematic.png

[polidea]: https://github.com/Polidea/Polithings/tree/master/numpad
[ciromattia]: https://github.com/ciromattia/Fritzing-Library
