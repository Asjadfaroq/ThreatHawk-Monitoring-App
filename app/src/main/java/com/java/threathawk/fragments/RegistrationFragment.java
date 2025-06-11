package com.java.threathawk.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.java.threathawk.Helper.UserDatabaseHelper;
import com.java.threathawk.R;
import com.java.threathawk.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 123;
    private static final String CAMERA = Manifest.permission.CAMERA;

    private static final int REQUEST_CODE_TAKE_PHOTO = 101;
    private static final int REQUEST_CODE_PICK_PHOTO = 102;
    ImageView profileImage;

    private UserDatabaseHelper databaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        databaseHelper = new UserDatabaseHelper(requireContext());

        Button saveButton = view.findViewById(R.id.set_cred);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick(v);
            }
        });
        return view;
    }

    public void onProfileImageClick(View view) {
        showOptionsDialog();
    }

    private void onSaveButtonClick() {
        // Retrieve user information from UI components
        EditText usernameEditText = requireView().findViewById(R.id.reg_name);
        EditText emailEditText = requireView().findViewById(R.id.reg_email);
        EditText passwordEditText = requireView().findViewById(R.id.reg_pass);

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the user information in the SQLite database
        long result = databaseHelper.insertUser(username, email, password, null); // You can replace null with the profile image path

        if (result != -1) {
            // Successful insertion
            Toast.makeText(requireContext(), "User information saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Error during insertion
            Toast.makeText(requireContext(), "Error saving user information", Toast.LENGTH_SHORT).show();
        }
    }


    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select an option")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            launchCamera();
                            break;
                        case 1:
                            pickPhotoFromGallery();
                            break;                    }
                });
        builder.create().show();
    }

    private void launchCamera() {
        if (isCameraAvailable()) {
            // Create a file to save the captured image
            openCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private void openCamera() {
        // Logic to open the camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    private boolean isCameraAvailable() {
        return ContextCompat.checkSelfPermission(requireContext(), CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    private void loadImage(Uri uri) {
        // Load the selected image into the ImageView
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            profileImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed to launch camera
                openCamera();
            } else {
                // Camera permission denied
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && data != null) {
                // Handle the captured photo from the camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profileImage.setImageBitmap(imageBitmap);
                }
            } else if (requestCode == REQUEST_CODE_PICK_PHOTO && data != null) {
                // Handle the selected image from the gallery
                Uri selectedImageUri = data.getData();
                loadImage(selectedImageUri);
            }
        }
    }



}