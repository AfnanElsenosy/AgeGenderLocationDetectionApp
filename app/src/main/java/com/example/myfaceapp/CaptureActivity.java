package com.example.myfaceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CaptureActivity extends AppCompatActivity  {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private AlertDialog instructionDialog;
    private boolean cameraOptionSelected = false;

    Button capBtn;
    ProgressBar loadingProgressBar;
    GPSTracker gpsTracker;
    TextView Loading;
    private boolean instructionShown = false;
    private static final String KEY_INSTRUCTION_SHOWN = "instructionShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        Loading = findViewById(R.id.Loading);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        gpsTracker = new GPSTracker(this);

        if (savedInstanceState != null) {
            instructionShown = savedInstanceState.getBoolean(KEY_INSTRUCTION_SHOWN, false);
        }

        if (checkLocationPermission()) {
            capturePhotoWithLocation();
        } else {
            requestLocationPermission();
        }
    }

//
//        capBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhotoWithLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void capturePhotoWithLocation() {
        if (isGPSEnabled()) {
            cameraOptionSelected = true; // Set cameraOptionSelected to true
            if (!instructionShown) {
                showInstructionDialog();
                instructionShown = true;
            }
        } else {
            enableGPS();
        }
    }
    private void startCamera() {
        ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                uploadFile(uri);
            } else {
                Toast.makeText(this, "Invalid file URI", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isGPSEnabled() {
        return gpsTracker.isGPSEnabled();
    }

    private void enableGPS() {
        // Prompt the user to enable GPS
        new AlertDialog.Builder(this)
                .setTitle("Enable GPS")
                .setMessage("GPS is required for capturing the photo. Do you want to enable it?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Open GPS settings
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or display a message
                        Toast.makeText(CaptureActivity.this, "GPS is required for capturing the photo.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void uploadFile(Uri fileUri) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        Loading.setText("Loading..");

        try {
            File file = convertUriToFile(fileUri);
            uploadFileToServer(file);

        } catch (Exception e) {
            Log.e("TAG", "Error: " + e.getMessage());
        }
    }

    private File convertUriToFile(Uri fileUri) throws Exception {
        if (fileUri == null) {
            throw new Exception("Uri is null");
        }

        File file = new File(Objects.requireNonNull(fileUri.getPath()));

        if (!file.exists()) {
            throw new Exception("File does not exist: " + file.getPath());
        }

        return file;
    }

    private void uploadFileToServer(File file) {
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        LuxandApiService service = ServiceGenerator.createService(LuxandApiService.class);

        new Thread(() -> {
            try {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String country = gpsTracker.getCountryName();
                String city = gpsTracker.getCityName();
                String location = String.format(country+", "+city);

                // Check if activity is finishing before executing the call
                if (!isFinishing()) {
                    Call<ResponseBody> call = service.detectFace("c4da8898b06a4c2b95d26c62caccd1fd", body);
                    Response<ResponseBody> response = call.execute();
                    if (response.isSuccessful() && response.body() != null) {
                        handleResponse(response.body(), file, location);
                    } else {
                        Log.e("TAG", "Server error: " + response.message());
                    }
                }
            } catch (IOException e) {
                Log.e("TAG", "Error: " + e.getMessage());
            }
        }).start();

    }

    private void handleResponse(ResponseBody responseBody, File file, String location) {
        Log.d("TAG", "Handling API response...");
        Gson gson = new Gson();
        try {
            String responseString = responseBody.string();
            Log.i("TAG", "API response: " + responseString);
            if (responseString.isEmpty()) {
                //Toast.makeText(CaptureActivity.this, "The response is empty , take a photo again.", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "API response is empty.");

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openDisplayImageActivity(Uri.fromFile(file), responseString, location);
                    }
                });
            }
        } catch (IOException e) {
            Log.e("TAG", "Error reading response: " + e.getMessage());
        } finally {
            responseBody.close();
        }
    }

    private void showInstructionDialog() {
        // Show a dialog with instructions for capturing the photo
        instructionDialog = new AlertDialog.Builder(this)
                .setTitle("Capture Photo")
                .setMessage("Note: to get a better result when using the camera make it straight to your " +
                        "face with 90 degrees or turn the phone 180 degrees to take a photo")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startCamera();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or display a message
                        Toast.makeText(CaptureActivity.this, "Photo capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // Do nothing or display a message
                        Toast.makeText(CaptureActivity.this, "Photo capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        instructionShown = true; // Set instructionShown to true after showing the dialog
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_INSTRUCTION_SHOWN, instructionShown);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the instruction dialog if it is shown and the activity is destroyed
        if (instructionDialog != null && instructionDialog.isShowing()) {
            instructionDialog.dismiss();
        }
    }



    private void openDisplayImageActivity(Uri fileUri, String response, String location) {
        loadingProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(CaptureActivity.this, ResultActivity.class);
        intent.putExtra("fileUri", fileUri);
        intent.putExtra("response", response);
        intent.putExtra("location", location);
        startActivity(intent);
    }
}
