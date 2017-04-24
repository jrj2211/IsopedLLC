package com.ravensclaw.isoped.isoped.helpers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.activities.MainActivity;
import com.ravensclaw.isoped.isoped.services.BluetoothLeService;

/**
 * Created by Joe on 4/3/2017.
 */

public class IsopedController {

    private Context mContext;
    private List<BluetoothGattService> mGattServices;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mSpeedCharacteristic;
    private BluetoothGattCharacteristic mStrideCharacteristic;
    private BluetoothGattCharacteristic mElevationCharacteristic;

    public static final int MAX_SPEED = 10;
    public static final int MIN_SPEED = 0;
    public static final int MAX_STRIDE = 15;
    public static final int MIN_STRIDE = 0;

    public IsopedController(Context context, BluetoothLeService bluetoothLeService, List<BluetoothGattService> services) {
        mContext = context;
        mGattServices = services;
        mBluetoothLeService = bluetoothLeService;

        // Loops through available GATT Services.
        for (BluetoothGattService service : mGattServices)
        {
            if(service.getUuid().toString().equals(mContext.getString(R.string.gatt_elevation_service))) {
                mElevationCharacteristic = service.getCharacteristic(UUID.fromString(mContext.getString(R.string.gatt_elevation_characteristic)));
            } else if(service.getUuid().toString().equals(mContext.getString(R.string.gatt_motor_service))) {
                mSpeedCharacteristic = service.getCharacteristic(UUID.fromString(mContext.getString(R.string.gatt_motor_control_characteristic)));
                mStrideCharacteristic = service.getCharacteristic(UUID.fromString(mContext.getString(R.string.gatt_motor_stride_characteristic)));
            }
        }
    }

    public void setAssistanceLevel(int level) {
        if(level > MAX_SPEED) {
            level = MAX_SPEED;
        }

        if(level < MIN_SPEED) {
            level = MIN_SPEED;
        }

        byte[] bytes = new byte[1];
        bytes[0] = (byte) level;
        mSpeedCharacteristic.setValue(bytes);
        mBluetoothLeService.writeCharacteristic(mSpeedCharacteristic);
    }

    public void setResistanceLevel(int level) {
        if(level > MAX_SPEED) {
            level = MAX_SPEED;
        }

        if(level < MIN_SPEED) {
            level = MIN_SPEED;
        }

        byte[] bytes = new byte[1];
        bytes[0] = (byte) (level * -1);
        mSpeedCharacteristic.setValue(bytes);
        mBluetoothLeService.writeCharacteristic(mSpeedCharacteristic);
    }

    public void setStrideLength(int length) {
        if(length > MAX_STRIDE) {
            length = MAX_STRIDE;
        }

        if(length < MIN_STRIDE) {
            length = MIN_STRIDE;
        }

        byte[] bytes = new byte[1];
        bytes[0] = (byte) (length & 0xff);
        Log.e("fdsfsd", Integer.toString((int) bytes[0]));
        mStrideCharacteristic.setValue(bytes);
        mBluetoothLeService.writeCharacteristic(mStrideCharacteristic);
    }

    public void getElevationAngle() {
        mBluetoothLeService.readCharacteristic(mElevationCharacteristic);
    }

}
