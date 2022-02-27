package com.example.lightswimhci;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

// singleton
public final class ArduinoManager {

    private static ArduinoManager instance = null;

    private final Arduino arduino;

    private ArduinoManager(Context context, TextView statusText) {
        arduino = new Arduino(context);
        arduino.addVendorId(6790);
        arduino.setBaudRate(9600);
        arduino.setArduinoListener(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
                arduino.open(device);
                statusText.setText(R.string.arudino_detected);
            }

            @Override
            public void onArduinoDetached() {
                // arduino detached from phone
                statusText.setText(R.string.arduino_not_detected);
                statusText.invalidate();
            }

            @Override
            public void onArduinoMessage(byte[] bytes) {
                String message = new String(bytes);
                // new message received from arduino
            }

            @Override
            public void onArduinoOpened() {
                // you can start the communication
                String str = "Hello Arduino !";
                arduino.send(str.getBytes());
            }

            @Override
            public void onUsbPermissionDenied() {
                // Permission denied, display popup then
                arduino.reopen();
            }
        });
    }

    public static boolean isOpen(){
        if (instance == null){
            return false;
        }
        return instance.arduino.isOpened();
    }

    public static ArduinoManager getInstance(){
        if (instance == null) {
            return null;
        }
        return instance;
    }


    public static ArduinoManager getInstance(Context context, TextView textToUpdate){
        if (instance == null) {
            instance = new ArduinoManager(context, textToUpdate);
        }
        return instance;
    }

    @NotNull
    public Arduino getArduino() {
        return arduino;
    }
}
