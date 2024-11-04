package com.example.habittracker3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker3.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextRegisterName, editTextRegisterSurname, editTextRegisterEmail, editTextRegisterCell, editTextRegisterUsername, editTextRegisterPassword;
    private Button buttonRegister, buttonAlreadyHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize UI elements
        editTextRegisterName = findViewById(R.id.editTextRegisterName);
        editTextRegisterSurname = findViewById(R.id.editTextRegisterSurname);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterCell = findViewById(R.id.editTextRegisterCell);
        editTextRegisterUsername = findViewById(R.id.editTextRegisterUsername);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonAlreadyHaveAccount = findViewById(R.id.buttonAlreadyHaveAccount);

        // Register button click listener
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Already have an account button click listener
        buttonAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void registerUser() {
        // Gather input data
        String name = editTextRegisterName.getText().toString().trim();
        String surname = editTextRegisterSurname.getText().toString().trim();
        String email = editTextRegisterEmail.getText().toString().trim();
        String cell = editTextRegisterCell.getText().toString().trim();
        String username = editTextRegisterUsername.getText().toString().trim();
        String password = editTextRegisterPassword.getText().toString().trim();

        // Validate input fields (you can add more validation as needed)
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || cell.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you would typically send the registration data to your server or database
        // For demonstration, we'll just show a success message
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

        // Optionally clear the fields after successful registration
        clearFields();
    }

    private void clearFields() {
        editTextRegisterName.setText("");
        editTextRegisterSurname.setText("");
        editTextRegisterEmail.setText("");
        editTextRegisterCell.setText("");
        editTextRegisterUsername.setText("");
        editTextRegisterPassword.setText("");
    }

    private void goToLogin() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: finish the registration activity if you don't want to return to it
    }
}
