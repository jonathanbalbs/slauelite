package com.example.slauelite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        hideKeyboard(findViewById(R.id.container));

        statusProgressBar = (ProgressBar) findViewById(R.id.login_progress);
        statusProgressBar.setVisibility(View.GONE);

        emailEx = (EditText) findViewById(R.id.registration_number);
        passwordEx = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    String url = "https://slauelite.000webhostapp.com/user/login.php";

                    try {
                        JSONObject dataObject = new JSONObject();
                        dataObject.put("email", registrationNumber);
                        dataObject.put("password", password);

                        JSONObject requestObject = new JSONObject();
                        requestObject.put("COMMAND", "login");
                        requestObject.put("DATA", dataObject);

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    String responseMessage = jsonObject.getString("message");

                                    if (responseMessage.equals("true")) {
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

                                        db.execSQL(uc.getSqlCreateUser());

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (responseMessage.equals("invalid")) {
                                        statusProgressBar.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        showError("Invalid credentials. Please enter the correct password and try again.");
                                    }else if (responseMessage.equals("false")) {
                                        statusProgressBar.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        showError("You not registered with this system. Click the register button to register.");
                                    }
                                } catch (JSONException e) {
                                    statusProgressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                    showError(e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                statusProgressBar.setVisibility(View.GONE);
                                loginButton.setVisibility(View.VISIBLE);
                                showError(volleyError.getMessage());
                            }
                        });

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        statusProgressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        showError(e.getMessage());
                    }

                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
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

    public void hideKeyboard(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
