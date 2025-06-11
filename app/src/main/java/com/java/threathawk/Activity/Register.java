package com.java.threathawk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.java.threathawk.R;

public class Register extends AppCompatActivity {

    Button setCred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setCred = findViewById(R.id.set_cred);
        setCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the second activity
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
}