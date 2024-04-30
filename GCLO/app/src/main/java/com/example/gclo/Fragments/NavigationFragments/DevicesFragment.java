package com.example.gclo.Fragments.NavigationFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Adapters.BluetoothAdapter.BtListAdapter;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.BluetoothViewModel;
import com.example.gclo.Models.BtListModel;
import com.example.gclo.Models.RetainedFragment;
import com.example.gclo.R;
import com.example.gclo.Utility.Constants;
import com.example.gclo.Utility.GlobalVariable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DevicesFragment extends Fragment implements BtListAdapter.OnItemClickListener {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    boolean bisBtConnected = false;
    boolean isPermissionGranted = true;
    UUID MY_UUID = UUID.fromString(Constants.uuid);

//    BluetoothAdapter bluetoothAdapter;
//        BluetoothSocket bluetoothSocket;
//    BluetoothDevice bluetoothDevice;
//    OutputStream outputStream;
//    InputStream inputStream;
    List<BtListModel> btListModels;
    private BluetoothViewModel bluetoothViewModel;
    BtListAdapter btListAdapter;

    String deviceName;
    String deviceAddress;
    RecyclerView recyclerView;
    TextView tvScan;
    Button btnConnect;
    private static final String TAG = "DEBUG_MA";
    RetainedFragment retainedFragment = new RetainedFragment();

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
//        listViewBt = view.findViewById(R.id.listviewBt);
        tvScan = view.findViewById(R.id.tvScan);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnConnect = view.findViewById(R.id.btnConnect);
        btListModels = new ArrayList<>();
        GlobalVariable.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        tvScan.setOnClickListener(view2 -> {
            bluetoothON();
            scan();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkBluetoothPermission();
        }
        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());
            }
        });

        btListAdapter = new BtListAdapter(getContext(), btListModels, this);
        recyclerView.setAdapter(btListAdapter);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariable.disconnectBluetoothDevice();
            }
        });
        return view;
    }//onCreateView

    /*public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }*/

    public void scan() {

        listDevices();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    private void bluetoothON() {
        if (GlobalVariable.bluetoothAdapter == null) {
            if (isPermissionGranted) {
                Toast.makeText(requireContext(), "Your device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else if (!GlobalVariable.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            isPermissionGranted = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                bluetoothON();
                Toast.makeText(getContext(), "Bluetooth permissions granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Permission denied
            Toast.makeText(getContext(), "Bluetooth permissions denied", Toast.LENGTH_SHORT).show();
        }
    }//onRequestPermissionsResult
    // get the response and handle it after permission granted
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(requireContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(requireContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @SuppressLint("MissingPermission")
    private void listDevices() {

        if (GlobalVariable.bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device");
            return;
        }
        Set<BluetoothDevice> pairedDevices = GlobalVariable.bluetoothAdapter.getBondedDevices();
        btListModels.clear();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();
                deviceAddress = device.getAddress();
                BtListModel btListModel = new BtListModel(deviceName, deviceAddress);
                btListModels.add(btListModel);
            }
            btListAdapter = new BtListAdapter(requireContext(), btListModels, this);
            recyclerView.setAdapter(btListAdapter);
            LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(linearLayout);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(String deviceAddress) {
        Log.d(TAG, "onItemClick - started");
        // Handle item click here
        Toast.makeText(requireContext(), "Selected device: " + deviceAddress, Toast.LENGTH_SHORT).show();
        // You can perform any actions based on the selected device name
//        loadFragment(new TerminalFragment());
        // Connect to the selected device
// Discover devices
//        if (!bluetoothAdapter.isDiscovering()) {
//            bluetoothAdapter.startDiscovery();
//        }
        // Start Bluetooth discovery (optional, you can skip this if the device is already paired)
//        bluetoothAdapter.startDiscovery();
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        bluetoothViewModel.setBluetoothDevice(bluetoothAdapter.getRemoteDevice(deviceAddress));
//        bluetoothDevice = bluetoothViewModel.getBluetoothDevice();
        GlobalVariable.bluetoothDevice = GlobalVariable.bluetoothAdapter.getRemoteDevice(deviceAddress);
        Log.d("bluetooth", "Bluetooth device: " +
                GlobalVariable.bluetoothDevice);
        connectToDevice(deviceAddress);
    }

    public void connectToDevice(String deviceAddress) {
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                try {
                    // Create a Bluetooth socket
                    GlobalVariable.bluetoothSocket = GlobalVariable.bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                    bluetoothViewModel.setBluetoothSocket(bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID));
//                    bluetoothSocket = bluetoothViewModel.getBluetoothSocket();
                    Log.d(TAG, "Bluetooth socket: " + GlobalVariable.bluetoothSocket);
//                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                    bluetoothAdapter.cancelDiscovery();
                    // Connect to the remote device through the socket. This is a blocking call.
//                    GlobalVariable.bluetoothSocket.connect();
                    GlobalVariable.bluetoothSocket.connect();

                    // After successfully connecting to the Bluetooth device keep the BluetoothSocket object into retainedfragment

//                    retainedFragment.setBluetoothSocket(bluetoothSocket);
//                    bluetoothViewModel.setBluetoothSocket(bluetoothSocket);

                    // If the connection is successful, you can get the OutputStream to send data
//                    outputStream = bluetoothSocket.getOutputStream();
                    // Notify the user that the connection was successful
//                    store connected bluetooth device and bluetoothSocket
//                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("BluetoothPref", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("remoteDevice", bluetoothDevice.toString());
//                    editor.putString("bluetoothSocket", bluetoothSocket.toString());
//                    editor.apply();

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Connected to: " + GlobalVariable.bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                        btnConnect.setText("Disconnect");
                        bisBtConnected = true;
                        ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                        ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Connection failed", Toast.LENGTH_SHORT).show();
//                        btnConnect.setText("Connect");
//                        bisBtConnected = false;
                    });

                    try {
//                        GlobalVariable.bluetoothSocket.close();
                        GlobalVariable.bluetoothSocket.close();
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                }
            }
        }).start();
    }

/*

    public void disconnectBluetoothDevice(){
        try{
            if(GlobalVariable.bluetoothSocket != null){
                GlobalVariable.bluetoothSocket.close();
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream=null;
            }
            if(inputStream!=null){
                inputStream.close();
                inputStream=null;
            }
            if(GlobalVariable.bluetoothDevice!=null){
                GlobalVariable.bluetoothDevice=null;
            }
            Log.d(TAG, "Socket closed");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
*/

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            Log.d(TAG, "Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}//DevicesFragment
/*


Wrote stack traces to tombstoned
2024-04-18 20:40:34.896  1634-11911 ActivityManager         system_server                        E  ANR in com.example.gclo (com.example.gclo/.MainActivity)
                                                                                                    PID: 6928
                                                                                                    Reason: Input dispatching timed out (65026a6 com.example.gclo/com.example.gclo.MainActivity (server) is not responding. Waited 8000ms for MotionEvent(action=DOWN))
                                                                                                    Parent: com.example.gclo/.MainActivity
                                                                                                    ErrorId: 53e49470-a3b4-4cfc-8fd9-8314ad3f9265
                                                                                                    Frozen: false
                                                                                                    Load: 0.0 / 0.0 / 0.0
                                                                                                    ----- Output from /proc/pressure/memory -----
                                                                                                    some avg10=0.26 avg60=0.98 avg300=0.49 total=1191451009
                                                                                                    full avg10=0.06 avg60=0.13 avg300=0.06 total=393049760
                                                                                                    ----- End output from /proc/pressure/memory -----

                                                                                                    CPU usage from 1ms to 5536ms later (2024-04-18 20:40:29.297 to 2024-04-18 20:40:34.832):
                                                                                                      98% 6928/com.example.gclo: 95% user + 2.9% kernel / faults: 8852 minor 4414 major
                                                                                                        95% 6928/om.example.gclo: 93% user + 2.1% kernel
                                                                                                      20% 1634/system_server: 8.3% user + 11% kernel / faults: 5321 minor 854 major
                                                                                                        9% 11911/AnrConsumer: 1.9% user + 7% kernel
                                                                                                        4.8% 1756/Signal Catcher: 2.7% user + 2.1% kernel
                                                                                                      9.8% 29392/com.mi.globalminusscreen: 5.4% user + 4.3% kernel / faults: 1752 minor 197 major
                                                                                                        6.1% 29392/obalminusscreen: 3.4% user + 2.7% kernel
                                                                                                        3% 29416/Signal Catcher: 1.8% user + 1.2% kernel
                                                                                                      7.6% 1918/media.swcodec: 3.4% user + 4.1% kernel / faults: 6906 minor 57 major
                                                                                                      7.4% 1284/surfaceflinger: 3.9% user + 3.4% kernel / faults: 161 minor 4 major
                                                                                                        3.2% 1542/app: 1.9% user + 1.2% kernel
                                                                                                      5.4% 1244/media.hwcodec: 3.4% user + 1.9% kernel / faults: 4919 minor 81 major
                                                                                                      3.2% 20095/kworker/u16:6-devfreq_wq: 0% user + 3.2% kernel
                                                                                                      0% 1868/media.codec: 0% user + 0% kernel / faults: 3012 minor 112 major
                                                                                                      3% 3377/com.android.phone: 1.6% user + 1.4% kernel / faults: 1129 minor 659 major
                                                                                                        2.3% 3390/Signal Catcher: 1% user + 1.2% kernel
                                                                                                      3% 15296/adbd: 0.7% user + 2.3% kernel
                                                                                                        2% 15398/UsbFfs-worker: 0.3% user + 1.6% kernel
                                                                                                      3% 26289/kworker/u16:7-events_unbound: 0% user + 3% kernel
                                                                                                      2.8% 170/kswapd0:0: 0% user + 2.8% kernel
                                                                                                      2.9% 26290/kworker/u16:8-kverityd: 0% user + 2.9% kernel
                                                                                                      2.7% 6581/com.google.android.inputmethod.latin: 1.6% user + 1% kernel / faults: 1918 minor 590 major
                                                                                                        2.5% 6621/Signal Catcher: 1.6% user + 0.9% kernel
                                                                                                      2.3% 1733/media.extractor: 1% user + 1.2% kernel / faults: 2712 minor 75 major
                                                                                                      2.3% 6472/kworker/u16:0-adb: 0% user + 2.3% kernel
                                                                                                      1.8% 21388/kworker/u16:3-kverityd: 0% user + 1.8% kernel
                                                                                                      1.6% 555/logd: 0.3% user + 1.2% kernel / faults: 2 minor 2 major
                                                                                                      1.6% 1163/loop35: 0% user + 1.6% kernel
                                                                                                      1.4% 11/rcu_preempt: 0% user + 1.4% kernel
                                                                                                      1.2% 12/rcuog/0: 0% user + 1.2% kernel
                                                                                                      1.2% 39/rcuog/4: 0% user + 1.2% kernel
                                                                                                      1% 29612/kworker/u17:4-kgsl-events: 0% user + 1% kernel
                                                                                                      0.9% 58/rcuop/7: 0% user + 0.9% kernel
                                                                                                      0% 665/tombstoned: 0% user + 0% kernel / faults: 46 minor 158 major
                                                                                                      0.9% 1185/android.hardware.camera.provider@2.4-service_64: 0.1% user + 0.7% kernel / faults: 77 minor 123 major
                                                                                                      0.9% 3392/com.android.systemui: 0.7% user + 0.1% kernel / faults: 107 minor 3 major
                                                                                                      0.9% 11156/com.miui.securityadd: 0.9% user + 0% kernel / faults: 1435 minor
                                                                                                      0.9% 25571/logcat: 0.1% user + 0.7% kernel
                                                                                                      0.7% 1/init: 0.3% user + 0.3% kernel / faults: 2 minor 4 major
                                                                                                      0.5% 1181/android.hardware.audio.service: 0% user + 0.5% kernel / faults: 151 minor 65 major
                                                                                                      0.5% 1223/android.hardware.wifi@1.0-service: 0.3% user + 0.1% kernel / faults: 1 major
                                                                                                      0.5% 1230/vendor.qti.hardware.display.composer-service: 0.1% user + 0.3% kernel / faults: 97 minor 44 major
                                                                                                      0.5% 1276/audioserver: 0% user + 0.5% kernel / faults: 75 minor 42 major
                                                                                                      0.5% 15700/process-tracker: 0.1% user + 0.3% kernel
                                                                                                      0.5% 15708/process-tracker: 0% user + 0.5% kernel / faults: 1 minor
                                                                                                      0.5% 26288/kworker/u16:5-kverityd: 0% user + 0.5% kernel
                                                                                                      0.3% 21/rcuop/1: 0% user + 0.3% kernel
                                                                                                      0.3% 52/rcuop/6: 0% user + 0.3% kernel
                                                                                                      0.3% 559/hwservicemanager: 0% user + 0.3% kernel / faults: 72 minor 13 major
                                                                                                      0% 694/loop13: 0% user + 0% kernel
                                                                                                      0.3% 1184/android.hardware.bluetooth@1.0-service-qti: 0% user + 0.3% kernel / faults: 20 minor 16 major
2024-04-18 20:40:34.896  1634-11911 ActivityManager         system_server                        E    0.3% 1190/android.hardware.gnss@2.1-service-qti: 0% user + 0.3% kernel / faults: 74 minor 43 major
                                                                                                      0.3% 1193/android.hardware.health@2.1-service: 0% user + 0.3% kernel / faults: 11 minor 8 major
                                                                                                      0.3% 1725/cameraserver: 0% user + 0.3% kernel / faults: 106 minor 44 major
                                                                                                      0.3% 1737/mediaserver: 0.1% user + 0.1% kernel / faults: 63 minor 27 major
                                                                                                      0.3% 12331/irq/17-4520300.: 0% user + 0.3% kernel
                                                                                                      0.3% 25298/kworker/u17:1-dwc_wq: 0% user + 0.3% kernel
                                                                                                      0.3% 26268/kworker/u16:1-events_unbound: 0% user + 0.3% kernel
                                                                                                      0.1% 10/ksoftirqd/0: 0% user + 0.1% kernel
                                                                                                      0.1% 13/rcuop/0: 0% user + 0.1% kernel
                                                                                                      0.1% 27/rcuop/2: 0% user + 0.1% kernel
                                                                                                      0.1% 29/migration/3: 0% user + 0.1% kernel
                                                                                                      0.1% 33/rcuop/3: 0% user + 0.1% kernel
                                                                                                      0.1% 40/rcuop/4: 0% user + 0.1% kernel
                                                                                                      0.1% 46/rcuop/5: 0% user + 0.1% kernel
                                                                                                      0.1% 215/kworker/6:1H-kblockd: 0% user + 0.1% kernel
                                                                                                      0.1% 356/crtc_commit:76: 0% user + 0.1% kernel
                                                                                                      0% 459/loop1: 0% user + 0% kernel
                                                                                                      0% 596/vold: 0% user + 0% kernel / faults: 33 minor 25 major
                                                                                                      0% 626/android.system.suspend@1.0-service: 0% user + 0% kernel / faults: 53 minor 50 major
                                                                                                      0.1% 627/keystore2: 0% user + 0.1% kernel / faults: 22 minor 32 major
                                                                                                      0% 706/loop17: 0% user + 0% kernel
                                                                                                      0.1% 711/loop18: 0% user + 0.1% kernel
                                                                                                      0% 720/loop22: 0% user + 0% kernel
                                                                                                      0.1% 1044/netd: 0% user + 0.1% kernel / faults: 46 minor 17 major
                                                                                                      0% 1195/android.hardware.lights-service.qti: 0% user + 0% kernel / faults: 14 minor 18 major
                                                                                                      0.1% 1199/android.hardware.sensors@2.0-service.multihal: 0% user + 0.1% kernel / faults: 23 minor 22 major
                                                                                                      0.1% 1582/drmserver: 0% user + 0.1% kernel / faults: 69 minor 36 major
                                                                                                      0.1% 1664/kworker/u17:6: 0% user + 0.1% kernel
                                                                                                      0.1% 1734/media.metrics: 0% user + 0.1% kernel / faults: 45 minor 40 major
                                                                                                      0.1% 1876/cnd: 0.1% user + 0% kernel
                                                                                                      0.1% 1926/cnss-daemon: 0% user + 0.1% kernel
                                                                                                      0% 1939/gatekeeperd: 0% user + 0% kernel / faults: 31 minor 87 major
                                                                                                      0% 1953/android.hardware.biometrics.fingerprint@2.1-service: 0% user + 0% kernel / faults: 19 minor 24 major
                                                                                                      0.1% 4323/charge_logger: 0% user + 0.1% kernel
                                                                                                      0.1% 4349/msm_irqbalance: 0% user + 0.1% kernel
                                                                                                      0.1% 4446/kworker/1:0-pm: 0% user + 0.1% kernel
                                                                                                      0.1% 6587/kworker/2:0-events: 0% user + 0.1% kernel
                                                                                                      0.1% 6871/kworker/u16:9-adb: 0% user + 0.1% kernel
                                                                                                      0.1% 8121/kworker/5:1+pm: 0% user + 0.1% kernel
                                                                                                      0.1% 8616/com.google.android.apps.nbu.paisa.user: 0% user + 0.1% kernel / faults: 21 minor
                                                                                                      0.1% 10992/kworker/1:0H-kblockd: 0% user + 0.1% kernel
                                                                                                      0.1% 13262/com.google.android.gms.persistent: 0.1% user + 0% kernel / faults: 16 minor 1 major
                                                                                                      0.1% 15497/kworker/0:0H-kblockd: 0% user + 0.1% kernel
                                                                                                      0.1% 18329/kworker/u17:8-fsverity_read_queue: 0% user + 0.1% kernel
                                                                                                      0.1% 18483/com.google.android.googlequicksearchbox:search: 0.1% user + 0% kernel / faults: 5 minor
                                                                                                      0.1% 20294/scheduler_threa: 0% user + 0.1% kernel
                                                                                                      0.1% 21676/com.android.bluetooth: 0% user + 0.1% kernel
                                                                                                      0.1% 23955/com.google.android.gms: 0% user + 0.1% kernel / faults: 7 minor
                                                                                                      0.1% 27845/kworker/0:1-events: 0% user + 0.1% kernel
                                                                                                      0.1% 28773/kworker/3:2-events_freezable_power_: 0% user + 0.1% kernel
                                                                                                      0.1% 30504/com.drilens.wamr:notificationl: 0% user + 0.1% kernel / faults: 47 minor
                                                                                                      0.1% 31361/wpa_supplicant: 0% user + 0.1% kernel
                                                                                                      0.1% 31574/kworker/4:1H-kblockd: 0% user + 0.1% kernel
                                                                                                    31% TOTAL: 17% user + 10% kernel + 2.1% iowait + 0.9% irq + 0.4% softirq
                                                                                                    CPU usage from 163ms to 517ms later (2024-04-18 20:40:29.460 to 2024-04-18 20:40:29.814):
                                                                                                      101% 6928/com.example.gclo: 101% user + 0% kernel / faults: 95 minor 1 major
                                                                                                        97% 6928/om.example.gclo: 97% user + 0% kernel
                                                                                                        3.8% 7015/Jit thread pool: 3.8% user + 0% kernel
                                                                                                      62% 1634/system_server: 42% user + 19% kernel / faults: 1223 minor
                                                                                                        36% 11911/AnrConsumer: 16% user + 19% kernel
                                                                                                        29% 1764/HeapTaskDaemon: 29% user + 0% kernel
                                                                                                      9.7% 1284/surfaceflinger: 9.7% user + 0% kernel
                                                                                                        3.2% 1540/TimerDispatch: 0% user + 3.2% kernel
                                                                                                        3.2% 1542/app: 3.2% user + 0% kernel

 */