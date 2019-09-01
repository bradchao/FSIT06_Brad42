package tw.org.iii.appps.brad42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

public class MainActivity extends AppCompatActivity {
    private BluetoothClient mClient;

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed){
                Log.v("brad", "BLE On");
            }else{
                Log.v("brad", "BLE Off");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,},
                    123);
        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        mClient = new BluetoothClient(this);
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
    }


    public void test1(View view) {
        if (!mClient.isBluetoothOpened()){
            mClient.openBluetooth();
        }
    }

    public void test2(View view) {
        if (mClient.isBluetoothOpened()){
            mClient.closeBluetooth();
        }
    }

    public void test3(View view) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                Log.v("brad", "Start scan...");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                String mac = device.getAddress();
                String name = device.getName();
                int rssi = device.rssi;
                Log.v("brad", name + ":" + mac + ":" + rssi);
            }

            @Override
            public void onSearchStopped() {
                Log.v("brad", "Stop scan...");
            }

            @Override
            public void onSearchCanceled() {
                Log.v("brad", "cancel scan...");

            }
        });
    }

    public void test4(View view){
        mClient.stopSearch();
    }

    @Override
    public void finish() {
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        super.finish();
    }

}
