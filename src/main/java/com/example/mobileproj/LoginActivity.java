package com.example.mobileproj;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;

    Button login, searchButton;  // Add searchButton reference
    dbconnect db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new dbconnect(this);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        searchButton = findViewById(R.id.btn_search);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String hashedPassword = hashPassword(pass);
Log.e("pass", hashedPassword);
                try (Cursor cursor = db.login(user, hashedPassword)) {  // Now compare with hashed password
                    if (cursor != null && cursor.moveToFirst()) {
                        int roleIndex = cursor.getColumnIndex("role");
                        if (roleIndex != -1) {
                            String role = cursor.getString(roleIndex);
                            Intent intent = role.equals("admin")
                                    ? new Intent(LoginActivity.this, AdminActivity.class)
                                    : new Intent(LoginActivity.this, UserActivity.class);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Role information missing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Login error", e);
                }
            }
        });

        searchButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, getinfrbymat.class)));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            Log.e("HASH", "Hashing error", e);
            return null;
        }
    }
}