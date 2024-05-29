package com.example.gclo.Fragments.Profile;


import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.gclo.Models.UserModel;
import com.example.gclo.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditProfileFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int REQUEST_CHOOSER = 3;

    final String TAG = "my_debug";

    FirebaseAuth auth;
    Button btnCancel, btnSave;

    EditText etEPAddress, etEPEmail, etEPPhone, etEPPost, etEPGender;
    FirebaseUser firebaseUser;
    Uri imageUri;
    ImageView imgEPProfileImage;
    ProgressDialog progressDialog;
    TextView tvEPChangeProfile;
    // Add a variable to store the context
    private Context fragmentContext;
    TextView tvEPName, tvEPUsername;
    StorageReference storageReference;
    StorageTask uploadTask;
    View view;

    String userGender;
    UserModel userModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        tvEPChangeProfile = view.findViewById(R.id.tvEPChangeProfile);
        imgEPProfileImage = view.findViewById(R.id.imgEPProfileImage);
        tvEPUsername = view.findViewById(R.id.tvEPUsername);

        tvEPName = view.findViewById(R.id.tvEPName);
        etEPEmail = view.findViewById(R.id.etEPEmail);
        etEPPost = view.findViewById(R.id.etEPPost);
        etEPAddress = view.findViewById(R.id.etEPAddress);
        etEPPhone = view.findViewById(R.id.etEPPhone);
        etEPGender = view.findViewById(R.id.etEPGender);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        auth = firebaseAuth;

        firebaseUser = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(getContext());
        storageReference = FirebaseStorage.getInstance().getReference("/profile_photos/");

        tvEPChangeProfile.setOnClickListener(v -> {
//            galleryIntent();
            openImageChooser();
        });

        btnSave.setOnClickListener(v -> {
            userGender = etEPGender.getText().toString();
            save(etEPEmail.getText().toString(), etEPPost.getText().toString(), etEPAddress.getText().toString(), etEPPhone.getText().toString(), userGender);
            readCurrentUserData();
            replaceFragment(new ProfileFragment());
        });
        readCurrentUserData();
        getUserInfo();
        btnCancel.setOnClickListener(v -> replaceFragment(new ProfileFragment()));

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
                replaceFragment(new ProfileFragment());

            }
        });
        return view;
    }//onCreateView


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Store the context when the fragment is attached
        fragmentContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Release the stored context when the fragment is detached
        fragmentContext = null;
    }

    public void readCurrentUserData() {
//        get all the current user data from the local storage database
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Current", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "no name");
        String username = sharedPreferences.getString("username", "no username");
        String post = sharedPreferences.getString("post", "post");
        String email = sharedPreferences.getString("email", "");
        String address = sharedPreferences.getString("address", "local address");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "1234567890");
        String gender = sharedPreferences.getString("gender", "");

//        set the all current user data into layout
        tvEPName.setText(name);
        tvEPUsername.setText(username);
        etEPPost.setText(post);
        etEPEmail.setText(email);
        etEPAddress.setText(address);
        etEPPhone.setText(phoneNumber);
        etEPGender.setText(gender);
        Log.e("localData", "read successfully");
    }

    // get user information from database and show in the page
    public void getUserInfo() {
//        This function is created to show/display user's data in the EditProfileFragment

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
//                   all the data of the user are added in UserModel class. So, we are getting the data from the UserModel class
                    userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    String name = userModel.getName();
                    String username = userModel.getUsername();
                    String email = userModel.getEmail();
                    String post = userModel.getPost();
                    String address = userModel.getAddress();
                    String phone = userModel.getPhoneNumber();
                    String gender = userModel.getGender();

                    // get the current user data and save the data into local storage(SharedPreferences Database)
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CurrentUserData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("name", name);
                    editor.putString("username", username);
                    editor.putString("post", post);
                    editor.putString("email", email);
                    editor.putString("address", address);
                    editor.putString("phoneNumber", phone);
                    editor.putString("gender", gender);
                    Log.e("localGender", "Selected gender: " + gender);
                    editor.apply();
                    Log.e("localData", "current user data added");


                    tvEPName.setText(name);
                    tvEPUsername.setText(username);
                    etEPEmail.setText(email);// get data and display

                    etEPAddress.setText(address);
                    etEPPhone.setText(phone);
                    etEPPost.setText(post);
                    etEPGender.setText(gender);

                    if (userModel.getImageUrl() != null && !userModel.getImageUrl().equals("default")) {
                        Glide.with(requireContext()).load(userModel.getImageUrl()).into(imgEPProfileImage);
                    } else {
                        imgEPProfileImage.setImageResource(R.drawable.man);
                    }
                } catch (Exception e) {
//                    Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //            public void save () {
// get data from users and upload changed data on the database(realtime database)
    public void save(String email, String post, String address, String phoneNumber, String gender) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                here we are creating the nodes of the user profile information in the firebase database
//                from here we are getting data from user using edittext and it saves data into realtime database
                UserModel userModel = snapshot.getValue(UserModel.class);
                assert userModel != null;

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id","GCLO2024112");
                hashMap.put("name", userModel.getName());
                hashMap.put("username", userModel.getUsername());
                hashMap.put("email", email);
                hashMap.put("imageUrl", userModel.getImageUrl());
                hashMap.put("post", post);// get data and save into realtime database
                hashMap.put("address", address);
                hashMap.put("phoneNumber", phoneNumber);
                hashMap.put("gender", gender);
                Log.d(TAG, "Selected Gender: " + gender);
                Log.d(TAG,"save Image url: "+userModel.getImageUrl());

                databaseReference.setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//save

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void openImageChooser() {

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(imageUri, "image/*");
//        pickIntent.setType("image/*"); // or use instead of above line
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooseIntent = Intent.createChooser(pickIntent, "Select Image");
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooseIntent, REQUEST_CHOOSER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Handle the chosen image from either camera or gallery
            if (requestCode == REQUEST_CHOOSER) {
                if (data.getData() != null) {
                    // Image from gallery
                    // Uri selectedImageUri = data.getData();
                    imageUri = data.getData();
                    imgEPProfileImage.setImageURI(imageUri);
                    uploadProfileImage();
                    Log.e("uploadImage", "Uploaded from gallery");
                } else {
                    // Image from camera
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        assert imageBitmap != null;
                        imageUri = getImageUri(requireContext(), imageBitmap);
                        imgEPProfileImage.setImageURI(imageUri);
                        Log.d(TAG, "onActivityResult: image uri: " + imageUri);
                        uploadProfileImage();
                        Log.e("uploadImage", "Uploaded from camera");
                    }
                }
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }

    // Convert bitmap image to URI
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        Log.d(TAG, "getImageUri: local path: " + path);
        return Uri.parse(path);
    }

    private void uploadProfileImage() {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(this.firebaseUser.getUid() + ".jpg");
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String myUri = downloadUri.toString();
                    saveData(myUri, progressDialog);
                    Log.d(TAG, "uploadProfileImage: uri: " + myUri);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            });
        }
    }

    // we have created this method to save the image in the firebase storage
    private void saveData(String myUri, ProgressDialog progressDialog) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users").child(this.firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user == null) {
                    throw new AssertionError();
                } else if (snapshot.exists()) {
                    user.setImageUrl(myUri);
                    reference.setValue(user);
                    progressDialog.dismiss();
                    Log.d(TAG, "saveData: image saved: "+myUri);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
