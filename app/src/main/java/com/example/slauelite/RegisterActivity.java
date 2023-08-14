package com.example.slauelite;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner facultySpinner, courseSpinner, separatorSpinner, yearSpinner, separatorSpinner2;
    ProgressBar statusProgressBar;

    ArrayAdapter facultyAdapter, courseAdapter, yearAdapter;
    String faculty = null, course = null, year = null, name, registrationNumber, phone, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        statusProgressBar = findViewById(R.id.register_progress);
        statusProgressBar.setVisibility(View.GONE);
        facultySpinner = findViewById(R.id.faculty_spinner);

        facultyAdapter = ArrayAdapter.createFromResource(this, R.array.faculties, R.layout.spinner_item);
        facultyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        facultySpinner.setAdapter(facultyAdapter);
        facultySpinner.setOnItemSelectedListener(this);

        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            EditText nameEx = findViewById(R.id.full_name);
            EditText regEx = findViewById(R.id.reg_number);
            EditText phoneEx = findViewById(R.id.phone_number);
            EditText emailEx = findViewById(R.id.email);
            EditText pasEx = findViewById(R.id.password);
            EditText pasEx2 = findViewById(R.id.password2);

            CountryCodePicker countryCodePicker = findViewById(R.id.ccp);
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

                String url = "http://localhost/projects/slau/public/api/register";
                try {
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("email", email);
                    dataObject.put("registration_number", registrationNumber);
                    dataObject.put("faculty", faculty);
                    dataObject.put("course", course);
                    dataObject.put("year", year);
                    dataObject.put("phone_number", phone);
                    dataObject.put("password", password);
                    dataObject.put("name", name);
                    dataObject.put("device_name", Build.MODEL);

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, dataObject, jsonObject -> {
                        try {
                            String responseMessage = jsonObject.getString("message");

                            if (responseMessage.equals("true")) {
                                //insert data into local database
                                MyDatabaseHandler dbHandler = new MyDatabaseHandler(getApplicationContext());
                                SQLiteDatabase db = dbHandler.getWritableDatabase();

                                ContentValues cv = new ContentValues();
                                UserContract uc = new UserContract();

                                cv.put(uc.getEmail(), email);
                                cv.put(uc.getRegNumber(), registrationNumber);
                                cv.put(uc.getFaculty(), faculty);
                                cv.put(uc.getCourse(), course);
                                cv.put(uc.getYear(), year);
                                cv.put(uc.getPhone(), phone);
                                cv.put(uc.getPassword(), password);
                                cv.put(uc.getFullName(), name);
                                cv.put(uc.getAccessToken(), jsonObject.getString("access_token"));

                                db.insert(uc.getTableName(), null, cv);

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
                    }, volleyError -> {
                        registerButton.setVisibility(View.VISIBLE);
                        statusProgressBar.setVisibility(View.GONE);
                        showError(volleyError.getMessage());
                    });

                    requestQueue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    registerButton.setVisibility(View.VISIBLE);
                    statusProgressBar.setVisibility(View.GONE);
                    showError(e.getMessage());
                }


            }
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Integer[] faculties = { 0, R.array.education, R.array.art, R.array.technology, R.array.humanities, R.array.business };

        if (parent.getId() == R.id.faculty_spinner) {
            if (position > 0) {
                faculty = parent.getItemAtPosition(position).toString().toLowerCase();

                courseSpinner = findViewById(R.id.course_spinner);
                separatorSpinner = findViewById(R.id.separator_spinner);

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

                yearSpinner = findViewById(R.id.year_spinner);
                separatorSpinner2 = findViewById(R.id.separator_spinner2);

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
}
