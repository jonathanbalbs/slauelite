package com.example.slauelite;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String registrationNumber, password;
    Button loginButton, registerButton;
    ProgressBar statusProgressBar;
    EditText emailEx, passwordEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        statusProgressBar = findViewById(R.id.login_progress);
        statusProgressBar.setVisibility(View.GONE);

        emailEx = findViewById(R.id.registration_number);
        passwordEx = findViewById(R.id.password);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> {
            registrationNumber = emailEx.getText().toString().trim().toUpperCase();
            password = passwordEx.getText().toString();

            if (registrationNumber.equals("")) {
                showError("Please provide your registration number");
            }else if (!registrationNumber.contains("/") || registrationNumber.length() < 14) {
                showError("You have entered an invalid registration number");
            }else if (password.equals("")) {
                showError("Please enter your password to continue");
            }else {
                loginButton.setVisibility(View.GONE);
                statusProgressBar.setVisibility(View.VISIBLE);

                String url = "http://localhost/projects/slau/public/api/login";

                try {
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("email", registrationNumber);
                    dataObject.put("password", password);

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, dataObject, jsonObject -> {
                        try {
                            String responseMessage = jsonObject.getString("message");

                            switch (responseMessage) {
                                case "true":
                                    JSONObject dt = jsonObject.getJSONObject("data");
                                    MyDatabaseHandler dbHandler = new MyDatabaseHandler(getApplicationContext());
                                    SQLiteDatabase db = dbHandler.getWritableDatabase();

                                    ContentValues cv = new ContentValues();
                                    UserContract uc = new UserContract();

                                    cv.put(uc.getFullName(), dt.getString("name"));
                                    cv.put(uc.getEmail(), dt.getString("email"));
                                    cv.put(uc.getPhone(), dt.getString("phone"));
                                    cv.put(uc.getRegNumber(), dt.getString("reg_number"));
                                    cv.put(uc.getFaculty(), dt.getString("faculty"));
                                    cv.put(uc.getCourse(), dt.getString("course"));
                                    cv.put(uc.getYear(), dt.getString("year"));
                                    cv.put(uc.getAccessToken(), dt.getString("access_token"));

                                    db.execSQL(uc.getSqlCreateUser());

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case "invalid":
                                    statusProgressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                    showError("Invalid credentials. Please enter the correct password and try again.");
                                    break;
                                case "false":
                                    statusProgressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                    showError("You not registered with this system. Click the register button to register.");
                                    break;
                            }
                        } catch (JSONException e) {
                            statusProgressBar.setVisibility(View.GONE);
                            loginButton.setVisibility(View.VISIBLE);
                            showError(e.getMessage());
                        }
                    }, volleyError -> {
                        statusProgressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        showError(volleyError.getMessage());
                    });

                    requestQueue.add(jsonObjectRequest);

                } catch (JSONException e) {
                    statusProgressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                    showError(e.getMessage());
                }

            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.logo_square_sm);
        builder.setMessage(message);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
