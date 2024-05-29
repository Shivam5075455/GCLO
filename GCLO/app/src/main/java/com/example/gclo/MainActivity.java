package com.example.gclo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.gclo.Activity.LoginActivity;
import com.example.gclo.Fragments.NavigationFragments.AboutFragment;
import com.example.gclo.Fragments.NavigationFragments.ChatFragment;
import com.example.gclo.Fragments.NavigationFragments.DevicesFragment;
import com.example.gclo.Fragments.NavigationFragments.MapFragment;
import com.example.gclo.Fragments.NavigationFragments.PersonDetailsFragment;
import com.example.gclo.Fragments.NavigationFragments.SettingFragment;
import com.example.gclo.Fragments.NavigationFragments.TerminalFragment;
import com.example.gclo.Fragments.Profile.ProfileFragment;
import com.example.gclo.Models.BluetoothViewModel;
import com.example.gclo.Utility.GlobalVariable;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth auth;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    private BluetoothViewModel bluetoothViewModel;

    Button btnLogout;
    DevicesFragment devicesFragment = new DevicesFragment();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private InterstitialAd mInterstitialad;
    FirebaseUser firebaseUser;
    private static final int REQUEST_ENABLE_BT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.status_bar));
        setSupportActionBar(toolbar);



        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        changeToolbarTitle("Terminal");

        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

        verifyUser();


        /*if(GlobalVariable.bluetoothSocket == null){
            try {
                GlobalVariable.bluetoothSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/

//        checkBluetoothPermission();
    }//oncreate

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        sharedPreferences.edit().putString("profileID", auth.getCurrentUser().getUid()).apply();


    }//onStart



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        closeBluetoothAdapter();
        GlobalVariable.disconnectBluetoothDevice(this);
    }//onDestroy

    public void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        finish(); // Close the activity and exit the application
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, do nothing or handle accordingly

                    }
                });

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }//showExitConfirmationDialog

    public void changeToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }//changeToolbarTitle

    public void verifyUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
            finish();
        } else if (user != null && user.isEmailVerified()) {
            navigation();
            replaceFragments(new TerminalFragment());

        }
    }

    public void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSION);
        }
    }
// if permission is requested then get a response and handle it

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now disable Bluetooth
//                closeBluetoothAdapter();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
//                create a dialogue to get permission again

//                new AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("Location permission is required\nPlease grant")
//                        .setPositiveButton("Grant", new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                checkBluetoothPermission();
//                            }
//                        })
//                        .setNegativeButton("Deny", new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(MainActivity.this, "You deny the permission", Toast.LENGTH_SHORT).show();
//                                MainActivity.this.finish();
//                            }
//                        })
//                        .create();


            }
        }
    }//onRequestPermissionsResult

    //    @SuppressLint("MissingPermission")
//    private void closeBluetoothAdapter() {
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//            bluetoothAdapter.disable();
//        }
//    }


    @SuppressLint("MissingPermission")
    private void closeBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Check for BLUETOOTH_CONNECT permission dynamically
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.disable();
            } else {
                // Request BLUETOOTH_CONNECT permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BLUETOOTH_PERMISSION);
            }
        }
    }


    public void navigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menuTerminal) {
                replaceFragments(new TerminalFragment());
                changeToolbarTitle("Terminal");
            } else if (itemId == R.id.menuDevices) {
                replaceFragments(new DevicesFragment());
                changeToolbarTitle("Devices");
            } else if (itemId == R.id.menuMap) {
                replaceFragments(new MapFragment());
                changeToolbarTitle("Map");
            } else if (itemId == R.id.menuChat) {
                replaceFragments(new ChatFragment());
                changeToolbarTitle("Chat");
            } else if (itemId == R.id.menuPersonDetails) {
                replaceFragments(new PersonDetailsFragment());
                changeToolbarTitle("Person Details");
//                showInterstitialAd();

            } else if (itemId == R.id.menuProfile) {
                replaceFragments(new ProfileFragment());
                changeToolbarTitle("Profile");
            } else if (itemId == R.id.menuSetting) {
                replaceFragments(new SettingFragment());
                changeToolbarTitle("Setting");
            } else if (itemId == R.id.menuAboutus) {
                replaceFragments(new AboutFragment());
                changeToolbarTitle("About us");
            } else if (itemId == R.id.menuLogout) {
                if (auth.getCurrentUser() == null || !auth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }//navigation

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.searchview,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }


    private void showInterstitialAd(){

        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("2E781CC29A08B1744EF62F3A01BBBD70"))
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(MainActivity.this, getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialad=interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Toast.makeText(MainActivity.this, "Ad clicked", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Log.d(TAG, "Ad dismissed fullscreen content.");
                        mInterstitialad=null;
                        Toast.makeText(MainActivity.this, "dismissed fullscreen", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Log.e(TAG, "Ad failed to show fullscreen content.");
                        mInterstitialad = null;
                        Toast.makeText(MainActivity.this, "failed to show fullscreen", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d(TAG, "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d(TAG, "Ad showed fullscreen content.");
                        Toast.makeText(MainActivity.this, "Ad showed fullscreen", Toast.LENGTH_SHORT).show();
                    }
                });


            }//onAdLoaded
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mInterstitialad !=null){
                    mInterstitialad.show(MainActivity.this);
                }else{
                    Log.e("ad","ad pending");
                }
            }
        },5000);

    }//showInterstitialad



}
