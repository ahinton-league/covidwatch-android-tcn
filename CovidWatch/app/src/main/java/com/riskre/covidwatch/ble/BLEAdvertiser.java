package com.riskre.covidwatch.ble;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.riskre.covidwatch.UUIDs;
import com.riskre.covidwatch.UuidAdapter;

import java.util.UUID;

import static com.riskre.covidwatch.UUIDs.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC;

/**
 * BLEAdvertiser is responsible for advertising the bluetooth services.
 * Only one instance of this class is to be constructed, but its not enforced. (for now)
 * You have been warned!
 */
public class BLEAdvertiser {

    // Constants
    private static final String TAG = "BLEContactTracing";

    // BLE
    private BluetoothLeAdvertiser advertiser;
    private Context context;

    /**
     * Initializes the BluetoothLeScanner and BluetoothLeAdvertiser
     * from the given BluetoothAdapter
     *
     * @param ctx The context this object is in
     * @param adapter The default adapter to use for BLE
     */
    public BLEAdvertiser(Context ctx, BluetoothAdapter adapter) {
        context = ctx;
        advertiser = adapter.getBluetoothLeAdvertiser();
    }

    /**
     * Callback when advertisements start and stops
     */
    private final AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.w("BLE", "Advertising success!: " + settingsInEffect);
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("BLE", "Advertising onStartFailure: " + errorCode);
            super.onStartFailure(errorCode);
        }
    };

    /**
     * Starts the advertiser, with the given UUID. We advertise with MEDIUM power to get
     * reasonable range, but this will need to be experimentally determined later.
     * ADVERTISE_MODE_LOW_LATENCY is a must as the other nodes are not real-time.
     *
     * @param serviceUUID The UUID to advertise the service
     * @param contactEventUUID The UUID that indicates the contact event
     */
    public void startAdvertiser(UUID serviceUUID, UUID contactEventUUID) {

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(new ParcelUuid(serviceUUID))
                .addServiceData(new ParcelUuid(serviceUUID),
                        new UuidAdapter().getBytesFromUUID(contactEventUUID))
                .build();

        advertiser.startAdvertising(settings, data, advertisingCallback);
    }

    /**
     * Stops all BLE related activity
     */
    public void stopAdvertiser() {
        advertiser.stopAdvertising(advertisingCallback);
    }

    /**
     * Changes the CEN to a new random valid UUID
     * NOTE: This will stop/start the advertiser
     */
    public void changeContactEventNumber() {
        Log.i(TAG, "Changing the contact event number!");
        this.stopAdvertiser();
        this.startAdvertiser(UUIDs.CONTACT_EVENT_SERVICE, UUID.randomUUID());
    }
}
