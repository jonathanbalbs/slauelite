package com.example.slauelite;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner facultySpinner, courseSpinner, separatorSpinner, yearSpinner, separatorSpinner2;
    ProgressBar statusProgressBar;

    ArrayAdapter facultyAdapter, courseAdapter, yearAdapter;
    String faculty = null, course = null, year = null, name, registrationNumber, phone, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide(Gravity.LEFT));
            getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        }
        setContentView(R.layout.activity_register);

        hideKeyboard(findViewById(R.id.container));

        statusProgressBar = (ProgressBar) findViewById(R.id.register_progress);
        statusProgressBar.setVisibility(View.GONE);
        facultySpinner = (Spinner) findViewById(R.id.faculty_spinner);

        facultyAdapter = ArrayAdapter.createFromResource(this, R.array.faculties, R.layout.spinner_item);
        facultyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        facultySpinner.setAdapter(facultyAdapter);
        facultySpinner.setOnItemSelectedListener(this);

        final Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEx = (EditText) findViewById(R.id.full_name);
                EditText regEx = (EditText) findViewById(R.id.reg_number);
                EditText phoneEx = (EditText) findViewById(R.id.phone_number);
                EditText emailEx = (EditText) findViewById(R.id.email);
                EditText pasEx = (EditText) findViewById(R.id.password);
                EditText pasEx2 = (EditText) findViewById(R.id.password2);

                CountryCodePicker countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
                countryCodePicker.registerCarrierNumberEditText(phoneEx);

                phone = countryCodePicker.getFormattedFullNumber();
                name = nameEx.getText().toString().toUpperCase();
                registrationNumber = regEx.getText().toString().toUpperCase();
                email = emailEx.getText().toString().trim().toUpperCase();
                password = pasEx.getText().toString();
                String password2 = pasEx2.getText().toString();

                if (name.isEmpty()) {
                    showError("Please enter your full name.");
                }else if (registrationNumber.isEmpty()) {
                    showError("Please provide your registration number to continue");
                }else if (faculty == null) {
                    showError("Please select a faculty to continue");
                }else if (course == null) {
                    showError("Please select a course to continue");
                }else if (year == null) {
                    showError("Please select your current year of study");
                }else if (phone.isEmpty()) {
                    showError("Please provide your phone number");
                }else if (email.isEmpty()) {
                    showError("Please provide your email address");
                }else if (password.isEmpty()) {
                    showError("Provide a password to continue");
                }else if (!registrationNumber.contains("/") || registrationNumber.length() < 14) {
                    showError("Provide a valid registration number to continue");
                }else if (!countryCodePicker.isValidFullNumber()) {
                    showError("Please provide a valid phone number");
                }else if (!email.contains("@") || !email.contains(".")) {
                    showError("Provide a valid email address to continue");
                }else if (password.length() < 6) {
                    showError("Password must be at least 6 characters long");
                }else if (!password.equals(password2)) {
                    showError("Your passwords do not match");
                }else {
                    registerButton.setVisibility(View.GONE);
                    statusProgressBar.setVisibility(View.VISIBLE);

                    String url = "https://slauelite.000webhostapp.com/user/register.php";
                    try {
                        JSONObject dataObject = new JSONObject();
                        dataObject.put("email", email);
                        dataObject.put("reg_number", registrationNumber);
                        dataObject.put("faculty", faculty);
                        dataObject.put("course", course);
                        dataObject.put("year", year);
                        dataObject.put("phone", phone);
                        dataObject.put("password", password);
                        dataObject.put("name", name);

                        JSONObject requestObject = new JSONObject();
                        requestObject.put("COMMAND", "register");
                        requestObject.put("DATA", dataObject);

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    String responseMessage = jsonObject.getString("message");

                                    if (responseMessage.equals("true")) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        registerButton.setVisibility(View.VISIBLE);
                                        statusProgressBar.setVisibility(View.GONE);
                                        showError("Registration failed: "+responseMessage);
                                    }
                                } catch (JSONException e) {
                                    showError(e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                registerButton.setVisibility(View.VISIBLE);
                                statusProgressBar.setVisibility(View.GONE);
                                showError(volleyError.getMessage());
                            }
                        });

                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        registerButton.setVisibility(View.VISIBLE);
                        statusProgressBar.setVisibility(View.GONE);
                        showError(e.getMessage());
                    }


                }
            }
        });

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Integer[] faculties = { 0, R.array.education, R.array.art, R.array.technology, R.array.humanities, R.array.business };

        if (parent.getId() == R.id.faculty_spinner) {
            if (position > 0) {
                faculty = parent.getItemAtPosition(position).toString().toLowerCase();

                courseSpinner = (Spinner) findViewById(R.id.course_spinner);
                separatorSpinner = (Spinner) findViewById(R.id.separator_spinner);

                courseSpinner.setVisibility(View.VISIBLE);
                separatorSpinner.setVisibility(View.VISIBLE);

                courseAdapter = ArrayAdapter.createFromResource(this, faculties[position], R.layout.spinner_item);
                courseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                courseSpinner.setAdapter(courseAdapter);
                courseSpinner.setOnItemSelectedListener(this);
            }
        } else if (parent.getId() == R.id.course_spinner) {
            if (position > 0) {
                course = parent.getItemAtPosition(position).toString();
                int resource;

                yearSpinner = (Spinner) findViewById(R.id.year_spinner);
                separatorSpinner2 = (Spinner) findViewById(R.id.separator_spinner2);

                yearSpinner.setVisibility(View.VISIBLE);
                separatorSpinner2.setVisibility(View.VISIBLE);

                switch (course.charAt(0)) {
                    case 'B':
                        if (course.equals("Bachelor of Telecommunication Engineering") || course.equals("Bachelor of Science in Computer Engineering")) {
                            resource = R.array.four_years;
                        }else {
                            resource = R.array.three_years;
                        }
                        break;
                    case 'D':
                        resource = R.array.two_years;
                        break;
                    default:
                        resource = R.array.one_year;
                        break;
                }

                yearAdapter = ArrayAdapter.createFromResource(this, resource, R.layout.spinner_item);
                yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                yearSpinner.setAdapter(yearAdapter);
                yearSpinner.setOnItemSelectedListener(this);
            }
        }else if (parent.getId() == R.id.year_spinner) {
            if (position > 0) {
                year = parent.getItemAtPosition(position).toString();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void hideKeyboard(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(RegisterActivity.this);
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
