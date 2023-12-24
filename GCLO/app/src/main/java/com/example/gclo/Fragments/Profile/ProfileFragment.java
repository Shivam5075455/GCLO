package com.example.gclo.Fragments.Profile;

import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.OnBackPressedCallback;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.gclo.Fragments.NavigationFragments.MapFragment;
import com.example.gclo.Fragments.NavigationFragments.TerminalFragment;
import com.example.gclo.MainActivity;
import com.example.gclo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    Button btnEdit;
    FirebaseUser firebaseUser;
    CircleImageView imgProfileImage;

    TextView tvAddress, tvEmail, tvPhoneNumber, tvPost, tvUsername, tvGender,tvName;
    SwipeRefreshLayout siwperefreshlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUserName);
        tvPost = view.findViewById(R.id.tvPost);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnEdit = view.findViewById(R.id.btnEdit);
        tvGender = view.findViewById(R.id.tvGender);
        imgProfileImage = view.findViewById(R.id.imgProfileImage);
        siwperefreshlayout = view.findViewById(R.id.siwperefreshlayout);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String profileID = sharedPreferences.getString("profileID", firebaseUser.getUid());

        getCurrentUserData();
        readLocalCurrentUserData();
        swipetoRefresh();

        btnEdit.setOnClickListener(v -> replaceFragment(new EditProfileFragment()));

        Toast.makeText(getContext(), "Swipe to refresh", Toast.LENGTH_SHORT).show();
        
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
                replaceFragment(new TerminalFragment());

            }
        });
        return view;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }
    public void readLocalCurrentUserData() {
//        Read current user data from local storage SQlite database
        SharedPreferences sp = getContext().getSharedPreferences("Current", Context.MODE_PRIVATE);
        String name = sp.getString("name", "local name");
        String username = sp.getString("username", "local username");
        String post = sp.getString("post", "local post");
        String email = sp.getString("email", "local email");
        String gender = sp.getString("gender", "");
        String address = sp.getString("address", "lcoal address");
        String phoneNumber = sp.getString("phoneNumber", "6162636465");

        tvName.setText(name);
        tvUsername.setText(username);
        tvPost.setText(post);
        tvEmail.setText(email);
        tvGender.setText(gender);
        tvAddress.setText(address);
        tvPhoneNumber.setText(phoneNumber);
//        siwperefreshlayout.setRefreshing(false);

    }
    public void swipetoRefresh() {
        siwperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readLocalCurrentUserData();
                getCurrentUserData();
                siwperefreshlayout.setRefreshing(false);
            }
        });
        siwperefreshlayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright), getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_dark), getResources().getColor(android.R.color.holo_red_light));
//        new Handler().postDelayed(() -> {
////            Toast.makeText(getContext(), "refreshing...", Toast.LENGTH_SHORT).show();
//            siwperefreshlayout.setRefreshing(false);
//        }, 3000);
    }
    private void getCurrentUserData() {
//        profileID contains current user data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users").child(auth.getCurrentUser().getUid());// profileId or child(auth.getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        tvName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                        tvUsername.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                        tvEmail.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                        tvAddress.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                        tvPost.setText(Objects.requireNonNull(snapshot.child("post").getValue()).toString());
                        tvPhoneNumber.setText(Objects.requireNonNull(snapshot.child("phoneNumber").getValue()).toString());
                        tvGender.setText(Objects.requireNonNull(snapshot.child("gender").getValue()).toString());
                        String imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();

                        if (imageUrl != null) {
                            if (imageUrl.equals("default")) {
                                imgProfileImage.setImageResource(R.drawable.man);
                            } else {
                                Glide.with(getContext()).load(imageUrl).into(imgProfileImage);
                            }
                        }
                    } else {

//                        readCurrentUserData();
//                        tvUsername.setText("No Username");
                        Log.e("readCurrentData","Readed");
                        /*
                        tvEmail.setText("No email");
                        tvPhoneNumber.setText("No phone number");
                        tvPost.setText("no post");
                        tvPost.setText("no post");
                        tvUsername.setText("No Username");
                        tvEmail.setText("No email");
                        tvPhoneNumber.setText("No phone number");
                        tvAddress.setText("no address");
                        */
                    }
                } catch (Exception e) {
//                    Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(getContext(), LoginActivity.class));

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
