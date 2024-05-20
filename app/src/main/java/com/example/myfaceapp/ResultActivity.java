package com.example.myfaceapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.FileNotFoundException;


import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {
    Button returnBtn;
    ImageView imageView4;

    TextView AgeResult,
            GenderResult,
            AgeGroupResult,
            LocationResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initViews();

        // Get the image URI and response from the intent
        Uri imageUri = getIntent().getParcelableExtra("fileUri"); // Correct key is "fileUri"
        String response = getIntent().getStringExtra("response");
        String addressText = getIntent().getStringExtra("location");


        // Load and display the image
        displayImage(imageUri);
        // Display the response
        displayResponse(response,addressText);


        returnBtn.setOnClickListener(v->{
            goToMainActivity();
        });



    }
    private void initViews() {

        returnBtn = findViewById(R.id.returnBtn);
        imageView4 = findViewById(R.id.imageView4);

        //Results: where you will put results of the model and location
        AgeResult = findViewById(R.id.AgeResult);
        GenderResult = findViewById(R.id.GenderResult);
        LocationResult = findViewById(R.id.LocationResult);
    }


    private void displayImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            ImageView imageView = findViewById(R.id.imageView4);
            imageView.setImageBitmap(imageBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayResponse(String response,String addressText) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(response);

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            if (jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                double age = jsonObject.getAsJsonPrimitive("age").getAsDouble();
                String gender = jsonObject.getAsJsonObject("gender").getAsJsonPrimitive("value").getAsString();

                Log.i("TAG", "Age: " + age);
                Log.i("TAG", "Gender: " + gender);
                Log.i("TAG-location", "Location: " + addressText);

                AgeResult.setText(String.valueOf(age));
                GenderResult.setText(gender);
                LocationResult.setText(addressText);
            }
            else{
                Toast.makeText(ResultActivity.this, "failed detection ,go to home screen " +
                        "and take photo again", Toast.LENGTH_LONG).show();
            }
        }

    }



    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

}
