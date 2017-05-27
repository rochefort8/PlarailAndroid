package hoegaarden1917.com.plarailandroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.le.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import java.util.UUID;

public class BleCentral  implements IBleActivity{
    private final static LocationAccesser locationAccesser = new LocationAccesser();

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private boolean isBleEnabled = false;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private BluetoothGattCharacteristic bleCharacteristic;

    private TextView receivedValueView;
    private TextView sendValueView;
    private boolean mSwitchStatus = false ;

    // 乱数送信用.
    private Random random = new Random();
    private Timer timer;
    private Context mContext ;
    private Handler mHandler ;

    public void toggleSwitch() {
        if ((bleGatt != null) && (bleCharacteristic != null)) {
            byte[] bytes = {mSwitchStatus == false ? (byte) 0x01 : (byte) 0x00};
            bleCharacteristic.setValue(bytes);
            bleGatt.writeCharacteristic(bleCharacteristic);
            mSwitchStatus = !mSwitchStatus ;
        }
    }

    public void onGpsIsEnabled(){
        // 2016.03.07現在GPSを要求するのが6.0以降のみなのでOnになったら新しいAPIでScan開始.
        this.startScanByBleScanner();
    }

    public BleCentral(Context context,Handler handler) {
        mContext = context ;
        mHandler = handler ;

        isBleEnabled = false;

        // Bluetoothの使用準備.
        bleManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();

        // Writeリクエスト用のタイマーをセット.
        timer = new Timer();

        if ((bleAdapter == null)
                || (! bleAdapter.isEnabled())) {
            // Intentでボタンを押すとonActivityResultが実行されるので、第二引数の番号を元に処理を行う.
        }
        else{
            // BLEが使用可能ならスキャン開始.
            this.scanNewDevice();
        }
    }
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("onConnectionState","Connected") ;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("onConnectionState","Disonnected") ;
                if (bleGatt != null){
                    bleGatt.close();
                    bleGatt = null;
                    Message msg = Message.obtain(mHandler, 0, "disconnected");
                    mHandler.sendMessage(msg);

                }
                isBleEnabled = false;
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            // Serviceが見つかったら実行.
            if (status == BluetoothGatt.GATT_SUCCESS) {


                List<BluetoothGattService> service = gatt.getServices();

                for (int i = 0; i < service.size();i++) {
                    BluetoothGattService s = service.get(i) ;
                    UUID uuid = s.getUuid() ;
                    Log.d("W",uuid.toString()) ;
                }

                BluetoothGattService bleService = gatt.getService(UUID.fromString(mContext.getString(R.string.uuid_service)));
                if (bleService != null){

                    bleCharacteristic = bleService.getCharacteristic(UUID.fromString(mContext.getString(R.string.uuid_characteristic)));
                    if (bleCharacteristic != null) {
                        bleGatt = gatt;
/*
                        byte[] bytes = { mSwitchStatus==false? (byte)0x01:(byte)0x00} ;
                        bleCharacteristic.setValue(bytes) ;
                        bleGatt.writeCharacteristic(bleCharacteristic) ;
*/
                        Message msg = Message.obtain(mHandler, 1, "connected");
                        mHandler.sendMessage(msg);

                        isBleEnabled = true;
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            // キャラクタリスティックのUUIDをチェック(getUuidの結果が全て小文字で帰ってくるのでUpperCaseに変換)
            /*
            if (mContext.getString(R.string.uuid_characteristic).equals(characteristic.getUuid().toString().toUpperCase())){
                runOnUiThread(
                        () -> {
                            // Peripheral側で更新された値をセットする.
                            receivedValueView.setText(characteristic.getStringValue(0));
                        });
            }*/
        }
    };
    private void scanNewDevice(){
        // OS ver.6.0以上ならGPSがOnになっているかを確認する(GPSがOffだとScanに失敗するため).
        this.startScanByBleScanner();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            locationAccesser.checkIsGpsOn(mContext, this);
        }
        // OS ver.5.0以上ならBluetoothLeScannerを使用する.
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.startScanByBleScanner();
        }
        else{
            /*
            // デバイスの検出.
            // BluetoothAdapter.LeScanCallback() - onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord).
            bleAdapter.startLeScan(
                    (final BluetoothDevice device, int rssi, byte[] scanRecord) -> {
                        runOnUiThread(
                                () -> {
                                    // スキャン中に見つかったデバイスに接続を試みる.第三引数には接続後に呼ばれるBluetoothGattCallbackを指定する.
                                    bleGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
                                });
                    });
                    */
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScanByBleScanner(){
        bleScanner = bleAdapter.getBluetoothLeScanner();

        // デバイスの検出.
        bleScanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice() ;
                String name = device.getName() ;
                if (name != null) {
                    Log.d("BBB", name);
                    if (name.equals("NaokyAndHiroky")) {
                        result.getDevice().connectGatt(mContext.getApplicationContext(), false, mGattCallback);
                    }
                }

                // スキャン中に見つかったデバイスに接続を試みる.第三引数には接続後に呼ばれるBluetoothGattCallbackを指定する.
//              result.getDevice().connectGatt(getApplicationContext(), false, mGattCallback);
            }

            @Override
            public void onScanFailed(int intErrorCode) {
                super.onScanFailed(intErrorCode);
            }
        });
    }

}