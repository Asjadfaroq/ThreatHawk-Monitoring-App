package com.java.threathawk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.java.threathawk.R;

public class Login extends AppCompatActivity {

    Button serverSetting;
    Button login;
    EditText username, pass;
    String savedEmail = "admin";
    String savedPassword = "123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText  emailEditText   = findViewById(R.id.username);
        EditText passwordEditText   = findViewById(R.id.login_pass);
        serverSetting = findViewById(R.id.login);

        serverSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredEmail = emailEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                Log.d("LoginActivity", "Entered email: " + enteredEmail);
                Log.d("LoginActivity", "Entered password: " + enteredPassword);


                if (enteredEmail.equals(savedEmail) && enteredPassword.equals(savedPassword)) {
                    // Display a success message
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Display an error message
                    Toast.makeText(Login.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }

                // Create an intent to start the second activity

            }
        });

    }
}