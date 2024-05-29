package com.example.gclo.Fragments.NavigationFragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;


import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gclo.Adapters.PersonDetails.AllPersonDetailsAdapter;
import com.example.gclo.Database.PersonDatabaseHelper;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.PersondetailModel;
import com.example.gclo.R;
import com.example.gclo.Utility.GlobalVariable;
import com.example.gclo.databinding.PersonDetailLayoutBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class PersonDetailsFragment extends Fragment {

    final static String TAG = "PersonDetailsFragment";

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    SearchView searchView;
    List<PersondetailModel> persondetailModelList;
    RecyclerView rvPersonDetails;
    AllPersonDetailsAdapter allPersonDetailsAdapter;
    PersonDatabaseHelper personDatabaseHelper;
    String localId;
    SwipeRefreshLayout swiperefreshlayoutPersonDetails;

    PersonDetailLayoutBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_details, container, false);
        View view1 = inflater.inflate(R.layout.person_detail_layout, container, false);
        binding = PersonDetailLayoutBinding.bind(view1);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

//        Button btnRead = view.findViewById(R.id.btnRead);
        rvPersonDetails = view.findViewById(R.id.rvPersonDetails);
        swiperefreshlayoutPersonDetails = view.findViewById(R.id.swiperefreshlayoutPersonDetails);
        searchView = view.findViewById(R.id.searchView);
        persondetailModelList = new ArrayList<>();
        personDatabaseHelper = new PersonDatabaseHelper(getContext());

        showDetails();
        readData();
        refresh();
        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press here
                // For example, you can navigate to another fragment or perform other actions
                // If you want to allow the default back button behavior, call isEnabled() before handling it

                // Uncomment the next line if you want to allow the default back button behavior
//                 if (isEnabled()) {
//                     super.handleOnBackPressed();
//                 }
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

//        getLocaitonData();
        return view;
    }//onCreateView

    private void refresh() {
        swiperefreshlayoutPersonDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                readData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperefreshlayoutPersonDetails.setRefreshing(false);
                    }
                }, 100);
            }
        });
    }// end of refresh

    private void filterList(String newText) {
        List<PersondetailModel> filterPersonDetail = new ArrayList<>();
        PersonDatabaseHelper personDatabaseHelper1 = new PersonDatabaseHelper(getContext());
        persondetailModelList = personDatabaseHelper1.readPersonsData();
        for (PersondetailModel item : persondetailModelList) {
            if (item.getId().toLowerCase().contains(newText.toLowerCase())
                    || item.getName().toLowerCase().contains(newText.toLowerCase())
                    || item.getUsername().toLowerCase().contains(newText.toLowerCase())
                    || item.getEmail().toLowerCase().contains(newText.toLowerCase())) {
                filterPersonDetail.add(item);
            }
        }
        allPersonDetailsAdapter = new AllPersonDetailsAdapter(getContext(), filterPersonDetail);
        rvPersonDetails.setAdapter(allPersonDetailsAdapter);
        //        set layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPersonDetails.setLayoutManager(linearLayoutManager);
    }// end of filterList


    @Override
    public void onStart() {
        super.onStart();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        requireActivity().getMenuInflater().inflate(R.menu.searchview, menu);

        // Retrieve the SearchView and set its listeners
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return true;
    }


    //    In this method, we are inserting/storing data on fiebase as well as in local storage
    public void showDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PersondetailModel persondetailModel = snapshot.getValue(PersondetailModel.class);
                assert persondetailModel != null;

                if (snapshot.exists()) {
                    for (DataSnapshot userDataSnapshot : snapshot.getChildren()) {
//                              id,name,username,email, gender,lat,long,in,out,dis
                        try {
                            String id = generatePersonId(); // Add a method to generate a unique ID
                            String name = Objects.requireNonNull(userDataSnapshot.child("name").getValue()).toString();
                            String username = Objects.requireNonNull(userDataSnapshot.child("username").getValue()).toString();
                            String email = Objects.requireNonNull(userDataSnapshot.child("email").getValue()).toString();
                            String gender = Objects.requireNonNull(userDataSnapshot.child("gender").getValue()).toString();
                            String post = Objects.requireNonNull(userDataSnapshot.child("post").getValue()).toString();

//                            persondetailModelList.add(?new PersondetailModel(id, name, username, email, gender,"27.345678","72.345678","in","200m"));
                            PersonDatabaseHelper databaseHelper = new PersonDatabaseHelper(requireActivity());
                            databaseHelper.insertPersonData(name, username, email, post, gender.substring(0, 1));
                            Log.e("insertData", "Local data created");
//                          update the data
                            databaseHelper.updatePersonData(username, email, post, gender);
                            Log.e("localUpdateData", "local data updated");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
// Notify the adapter that the data set has changed
                    if (allPersonDetailsAdapter != null) {
                        allPersonDetailsAdapter.notifyDataSetChanged();
                    }
                    AllPersonDetailsAdapter allPersonDetailsAdapter = new AllPersonDetailsAdapter(getContext(), persondetailModelList);
                    rvPersonDetails.setAdapter(allPersonDetailsAdapter);
//                            define layout for recycler view
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    rvPersonDetails.setLayoutManager(linearLayoutManager);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }//showDetails();

    // You need to implement a method to generate a unique ID for a person
    private String generatePersonId() {
        // Implement your logic to generate a unique ID (e.g., timestamp, UUID, etc.)
        return UUID.randomUUID().toString();
    }

    public void readData() {
        // getting our course array list from personDatabaseHelper class.
        persondetailModelList = personDatabaseHelper.readPersonsData();
        if (allPersonDetailsAdapter == null) {
            // on below line passing our array list to our adapter class.
            allPersonDetailsAdapter = new AllPersonDetailsAdapter(getContext(), persondetailModelList);
            rvPersonDetails.setAdapter(allPersonDetailsAdapter);
//        set layout manager on the recycler view
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rvPersonDetails.setLayoutManager(linearLayoutManager);
        } else {
            // Update the data set and notify the adapter that the data set has changed
            allPersonDetailsAdapter.updateData(persondetailModelList);
            allPersonDetailsAdapter.notifyDataSetChanged();
        }
        Log.e("readdata", "Local data read successfully");
    }//readData()

    public void readData1(PersonDatabaseHelper personDatabaseHelper) {
        Cursor cursor = personDatabaseHelper.readData12();
        StringBuilder data = new StringBuilder();
        new ArrayList();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                localId = cursor.getString(0);
                List<PersondetailModel> persondetailModelList1 = new ArrayList<>();
                AllPersonDetailsAdapter allPersonDetailsAdapter1 = new AllPersonDetailsAdapter(getContext(), persondetailModelList1);
                rvPersonDetails.setAdapter(allPersonDetailsAdapter1);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rvPersonDetails.setLayoutManager(linearLayoutManager);
                data.append(localId + " ");
            }
//            tvReadData.setText(data.toString());
        }
    }

    public void getLocaitonData(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "27.345678");
        String longitude = sharedPreferences.getString("longitude", "");
        String distance = sharedPreferences.getString("distance","");
        String zone = sharedPreferences.getString("zone","");
        sharedPreferences.edit().clear().apply();

//        List<PersondetailModel> persondetailModelList = new ArrayList<>();
//        persondetailModelList.add(new PersondetailModel("id","name", latitude,longitude,distance,zone));
//        allPersonDetailsAdapter = new AllPersonDetailsAdapter(getContext(), persondetailModelList);
//        rvPersonDetails.setAdapter(allPersonDetailsAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        rvPersonDetails.setLayoutManager(linearLayoutManager);


//        binding.tvPDPersonLat.setText(latitude);
//        binding.tvPDPersonLong.setText(longitude);
//        binding.tvPDPersonDistance.setText(distance);
//        binding.tvPDPersonZone.setText(zone);

        Log.e(TAG, latitude);
        Log.e(TAG, longitude);
        Log.e(TAG, distance);
        Log.e(TAG, zone);
    }
/*    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }*/



}
