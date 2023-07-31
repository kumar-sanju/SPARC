package com.smart.sparc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.smart.sparc.room.MultipartRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etUsername, etPassword, etEmail, etAddress, etAge, etPhoneNo;
    private TextView login;
    private Spinner spinnerRole;
    private Button btnSelectImage, btnRegister;
    private ImageView ivProfileImage;
    private Uri selectedImageUri;


    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 102;

    private Bitmap selectedBitmap;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnRegister = findViewById(R.id.btnRegister);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        login = findViewById(R.id.login);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_ids, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivProfileImage.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser() {
        // Get user input from EditText fields
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String phoneNo = etPhoneNo.getText().toString().trim();
        String roleId = spinnerRole.getSelectedItem().toString();

        int selectedSpinnerPosition = spinnerRole.getSelectedItemPosition();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()
                || address.isEmpty() || age.isEmpty() || phoneNo.isEmpty() || roleId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (selectedSpinnerPosition == 0){
            Toast.makeText(this, "Selected role can't be 0.", Toast.LENGTH_SHORT).show();
            return;
        }

            // Convert the Bitmap to a Base64 encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        MultipartRequest.DataPart dataPart = new MultipartRequest.DataPart("image.jpg",
                Base64.decode(String.valueOf(selectedBitmap), Base64.DEFAULT), "image/jpeg");

        Map<String, MultipartRequest.DataPart> dataPartParams = new HashMap<>();
        dataPartParams.put("img", dataPart);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("userName", username);
        params.put("password", password);
        params.put("email", email);
        params.put("address", address);
        params.put("roleId", roleId);
        params.put("age", age);
        params.put("phoneNumber", phoneNo);

        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, "http://164.164.122.69:8081/AndroidTest/api/auth/signUp",
                params, dataPartParams,
                response -> {
                    Toast.makeText(RegisterActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                    // Handle the server response here
                    Log.d("sanju", "onResponse: "+ response);
                    Intent intent = new Intent(RegisterActivity.this, UserDataActivity.class);
                    startActivity(intent);
                    finish();
                    clearForm();
                },
                error -> {
                    // Handle error here
                    Log.d("sanju", "onResponse: "+ error.toString());
                    Toast.makeText(RegisterActivity.this, "Failure VolleyError TimeoutError.", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(multipartRequest);

    }

    // Helper method to clear the form fields and selected image
    private void clearForm() {
        etName.setText("");
        etUsername.setText("");
        etPassword.setText("");
        etEmail.setText("");
        etAddress.setText("");
        etAge.setText("");
        etPhoneNo.setText("");
        spinnerRole.setSelection(0); // Set the first item as default in the spinner
        ivProfileImage.setImageResource(R.drawable.ic_launcher_background);
        selectedImageUri = null;
        selectedBitmap = null;
    }

//    private String getImagePathFromUri(Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            String imagePath = cursor.getString(column_index);
//            cursor.close();
//            return imagePath;
//        }
//        return null;
//    }

//    private void codeinRetrofit() {
        //        RequestBody nameBody = RequestBody.create(MediaType.parse("text/form-data"), name);
//        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/form-data"), username);
//        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/form-data"), password);
//        RequestBody emailBody = RequestBody.create(MediaType.parse("text/form-data"), email);
//        RequestBody addressBody = RequestBody.create(MediaType.parse("text/form-data"), address);
//        RequestBody ageBody = RequestBody.create(MediaType.parse("text/form-data"), age);
//        RequestBody phoneNoBody = RequestBody.create(MediaType.parse("text/form-data"), phoneNo);
//        RequestBody roleIdBody = RequestBody.create(MediaType.parse("text/form-data"), roleId);
//
//        MultipartBody.Part imagePart = null;
//        if (imagePath != null) {
//            File file = new File(imagePath);
////            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
////            RequestBody requestFile = RequestBody.create(MediaType.parse("file/*"), file);
////            MultipartBody.Part.createFormData("jpeg", file.getName(), requestFile);
//            imagePart = MultipartBody.Part.createFormData("img", file.getName(), requestFile);
//        }

//        uploadImageToServer(imagePath);

//        apiClient = ApiClient.getInstance();
//        ApiService apiService = apiClient.getApiService();
//
//        Call<ApiResponse> call = apiService.registerUser(nameBody, usernameBody, passwordBody, emailBody,
//                addressBody, ageBody, phoneNoBody, roleIdBody, imagePart);
//
//        call.enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(RegisterActivity.this, UserDataActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                    clearForm();
//                } else {
//                    Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
//                }
//
////                if (response.isSuccessful() && response.body() != null) {
////                    ApiResponse apiResponse = response.body();
////                    if (apiResponse.isSuccess()) {
////                        // Registration successful
////                        Toast.makeText(RegisterActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
////
////                        if (apiResponse.getData() != null) {
////                            UserData userData = apiResponse.getData();
//////                            String userDataString = "Name: " + userData.getName() + "\n"
//////                                    + "Username: " + userData.getUserName() + "\n"
//////                                    + "Email: " + userData.getEmail() + "\n"
//////                                    + "Address: " + userData.getAddress() + "\n"
//////                                    + "Age: " + userData.getAge() + "\n"
//////                                    + "Phone No: " + userData.getPhoneNumber() + "\n"
//////                                    + "Role ID: " + userData.getRoleId();
////                        }
////
////                        clearForm();
////                    } else {
////                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
////                    }
////                } else {
////                    Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
////                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, "Registration Failed. Please check your internet connection.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void uploadImageToServer(String encodedImage) {
//        String url = "http://164.164.122.69:8081/AndroidTest/api/auth/signUp";
//
//        MultipartRequest.DataPart dataPart = new MultipartRequest.DataPart("image.jpg",
//                Base64.decode(encodedImage, Base64.DEFAULT), "image/jpeg");
//
//        Map<String, MultipartRequest.DataPart> dataPartParams = new HashMap<>();
//        dataPartParams.put("img", dataPart);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("name", "name");
//        params.put("userName", "name");
//        params.put("password", "123456");
//        params.put("email", "name@gmail.com");
//        params.put("address", "name");
//        params.put("roleId", "9");
//        params.put("age", "19");
//        params.put("phoneNumber", "9090909090");
//
//        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, url,
//                params, dataPartParams,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Handle the server response here
//                        Log.d("sanju", "onResponse: "+ response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Handle error here
//                        Log.d("sanju", "onResponse: "+ error.toString());
//                    }
//                });
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(this).add(multipartRequest);
//    }
}