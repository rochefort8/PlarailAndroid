package hoegaarden1917.com.plarailandroid;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Parcelable;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by 0000920402 on 2017/05/23.
 */

public class PlarailObject extends Object {

    public PlarailObject (BleCentral bleCentral,BluetoothGatt gatt) {
        mGatt = gatt ;
        mBleCentral = bleCentral ;

        BluetoothDevice device = gatt.getDevice() ;
        String address = device.getAddress() ;

        Log.d("New PLA",address) ;
        mSwitch = false ;

    }
    public BluetoothGatt getGatt() { return mGatt; }

    public String getUniqueName() {
        mBleCentral.readCharacteristic(mGatt);
        return null ;
    }
    public void setUniqueName(String name) {
        byte[] bytes = new byte[1] ;
        bytes[0] = 0x02 ;
        byte[] b = name.getBytes() ;

        ByteBuffer byteBuf = ByteBuffer.allocate(bytes.length + b.length);
        byteBuf.put(bytes);
        byteBuf.put(b);
        byte[] c = byteBuf.array();

        mBleCentral.writeCharacteristic(mGatt,c);
    }


    public void toggleSwitch() {
        byte[] bytes = {mSwitch == false ? (byte) 0x01 : (byte) 0x00};

        mBleCentral.writeCharacteristic(mGatt,bytes);
        mSwitch = !mSwitch ;
    }
    BleCentral mBleCentral ;
    BluetoothGatt mGatt ;
    String name ;
    String imagePath ;
    boolean mSwitch ;
}
