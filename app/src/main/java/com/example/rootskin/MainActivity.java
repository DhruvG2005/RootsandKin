package com.example.rootskin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import android.widget.Space;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.util.Locale;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button submitButton, FamHis;
    private Spinner GenderSpinner, SiblingSpinner, BloodGroupSpinner, MarriageSpinner, RoleSpinner;
    private EditText BdateEditText, FirstNameEditText, LastNameEditText, FatherNameEditText, MotherNameEditText, SiblingNameEditText, BPlaceEditText, SpouseEditText;
    private CheckBox SiblingCheckbox;
    private LinearLayout SiblingLayout1, SiblingInfo, MarriageInfo;
    private List<EditText> siblingFirstNameEditTexts = new ArrayList<>();
    private List<EditText> siblingLastNameEditTexts = new ArrayList<>();
    private int sr = 0;//sr=sibling relationship
    private FirebaseFirestore db;
    private String name, spouse, firstName, lastName, first_name, last_name, firstname, lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearSharedPreferences();
        backButton = findViewById(R.id.Back);
        submitButton = findViewById(R.id.Submit);
        GenderSpinner = findViewById(R.id.GenderSpinner);
        BdateEditText = findViewById(R.id.BdateEditText);
        FirstNameEditText = findViewById(R.id.FirstNameEditText);
        LastNameEditText = findViewById(R.id.LastNameEditText);
        FatherNameEditText = findViewById(R.id.FatherNameEditText);
        MotherNameEditText = findViewById(R.id.MotherNameEditText);
        SiblingNameEditText = findViewById(R.id.SiblingNameEditText);
        SiblingCheckbox = findViewById(R.id.SiblingCheckbox);
        SiblingLayout1 = findViewById(R.id.SiblingLayout1);
        SiblingInfo = findViewById(R.id.SiblingInfo);
        SiblingSpinner = findViewById(R.id.SiblingSpinner);
        BloodGroupSpinner = findViewById(R.id.BloodGroupSpinner);
        BPlaceEditText = findViewById(R.id.BPlaceEditText);
        FamHis = findViewById(R.id.FamHis);
        RoleSpinner = findViewById(R.id.RoleSpinner);
        MarriageSpinner = findViewById(R.id.MarriageSpinner);
        MarriageInfo = findViewById(R.id.MarriageInfo);
        SpouseEditText = findViewById(R.id.SpouseNameEditText);
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GenderSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.sibling_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SiblingSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.blood_group_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BloodGroupSpinner.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.marriage_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MarriageSpinner.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.role_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RoleSpinner.setAdapter(adapter4);

        // Set up the date picker for birth date...
        BdateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Initially hide the sibling layout
        SiblingLayout1.setVisibility(View.GONE);
        SiblingInfo.setVisibility(View.GONE);

        //Initially hide the marriage info layout
        MarriageInfo.setVisibility(View.GONE);

        SiblingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the sibling layout if the checkbox is checked
                SiblingLayout1.setVisibility(View.VISIBLE);
                SiblingInfo.setVisibility(View.GONE);
            } else {
                // Hide the sibling layout if the checkbox is unchecked
                SiblingLayout1.setVisibility(View.GONE);
                SiblingInfo.setVisibility(View.GONE);
            }
        });

        SiblingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                int numSiblings;

                try {
                    numSiblings = Integer.parseInt(selectedItem);
                } catch (NumberFormatException e) {
                    numSiblings = 0;  // Default to 0 if parsing fails
                }

                if (numSiblings > 0) {
                    SiblingInfo.setVisibility(View.VISIBLE);
                } else {
                    SiblingInfo.setVisibility(View.GONE);
                }

                updateSiblingFields(numSiblings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                SiblingInfo.setVisibility(View.GONE);
            }
        });

        // Set an item selected listener for MarriageSpinner
        MarriageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();

                if (selectedItem.equals("Married")) {
                    MarriageInfo.setVisibility(View.VISIBLE); // Show spouse info layout

                    // Determine the spouse based on gender
                    String gender = GenderSpinner.getSelectedItem().toString().trim();
                    if (gender.equalsIgnoreCase("Male")) {
                        spouse = "Wife";
                    } else if (gender.equalsIgnoreCase("Female")) {
                        spouse = "Husband";
                    } else {
                        spouse = "Null"; // Default if gender is not specified
                    }

                } else {
                    MarriageInfo.setVisibility(View.GONE); // Hide spouse info layout
                    spouse = "Null"; // Reset spouse if single
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                MarriageInfo.setVisibility(View.GONE);
                spouse = "Null"; // Reset spouse if nothing is selected
            }
        });

        backButton.setOnClickListener(v -> {
            showAlertDialog();
        });

        submitButton.setOnClickListener(v -> {
            // Validate required fields
            if (validateFields()) {
                // Proceed with submission
                sendDataToServer();
            } else {
                showAlertDialogError("Please fill in the Required Fields!!");
            }
        });

        FamHis.setOnClickListener(v -> {
            if(validateFieldsName())
                showFamilyTree();
            else
                showAlertDialogError("Please fill in the Required Fields!!");
        });
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it to the EditText
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    BdateEditText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }


    private void sendDataToServer() {
        first_name = FirstNameEditText.getText().toString().trim();
        last_name = LastNameEditText.getText().toString().trim();
//        name = first_name+last_name;
        String birth_date = BdateEditText.getText().toString().trim();
        String father_name = FatherNameEditText.getText().toString().trim();
        String mother_name = MotherNameEditText.getText().toString().trim();
        String gender = GenderSpinner.getSelectedItem().toString().trim();
        String blood_group = BloodGroupSpinner.getSelectedItem().toString().trim();
        String birth_place = BPlaceEditText.getText().toString().trim();
        String role = RoleSpinner.getSelectedItem().toString().trim();

        // Format the birthdate
        String formattedBirthDate = formatDate(birth_date);

        List<String> siblingNames = new ArrayList<>();
        int siblingCount = 0; // Default value for no siblings

        if(SiblingCheckbox.isChecked()) {
            // Increment sr for each sibling name entered
            for (int i = 0; i < siblingFirstNameEditTexts.size(); i++) {
                String sibling_first_name = siblingFirstNameEditTexts.get(i).getText().toString().trim();
                String sibling_last_name = siblingLastNameEditTexts.get(i).getText().toString().trim();

                // Ensure that both first and last names are not empty
                if (!sibling_first_name.isEmpty() && !sibling_last_name.isEmpty()) {
                    siblingCount++;
                    siblingNames.add(sibling_first_name + " " + sibling_last_name);
                }
            }
        }

        // If the sibling checkbox is unchecked, reset siblingNames to empty list and set siblingCount to 0
        if (!SiblingCheckbox.isChecked()) {
            siblingNames.clear();
            siblingCount = 0;
        }

        // Prepare data to store in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("sibling_count", siblingCount);
        userData.put("sibling_names", siblingNames);
        userData.put("Role", role);
        userData.put("Spouse Type", spouse);
        userData.put("Spouse Name", SpouseEditText.getText().toString().trim());

//        // Generate a valid document ID, ensure it's a unique and valid identifier.
//        String documentId = generateDocumentId(first_name.trim(), last_name.trim());

        // Store data in Firestore with 'name' as the document ID
        db.collection("users")
                .document(name) // Use 'name' as the document ID
                .set(userData) // Set the userData map as the document data
                .addOnSuccessListener(aVoid -> {
                    // Document successfully added
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + name);

                    // Reset the sibling counter after submission
                    sr = 0;
                    runOnUiThread(this::showAlertDialog1);
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        showAlertDialogError("Failed to submit data to Firestore: " + e.getMessage());
                    });
                });

        // URL encode the data to handle special characters
        try {
            String postData = "first_name=" + URLEncoder.encode(first_name, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(last_name, "UTF-8") +
                    "&father_name=" + URLEncoder.encode(father_name, "UTF-8") +
                    "&mother_name=" + URLEncoder.encode(mother_name, "UTF-8") +
                    "&birth_date=" + URLEncoder.encode(formattedBirthDate, "UTF-8") +
                    "&birth_place=" + URLEncoder.encode(birth_place, "UTF-8") +
                    "&gender=" + URLEncoder.encode(gender, "UTF-8") +
                    "&blood_group=" + URLEncoder.encode(blood_group, "UTF-8");

            // Create a new thread for network operation
            new Thread(() -> {
                HttpURLConnection urlConnection = null;
                try {
                    // URL to your PHP script
                    URL url = new URL("http://192.168.155.37/insert_person.php"); // Replace with your actual URL

                    // Create a HttpURLConnection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send the POST data
                    urlConnection.getOutputStream().write(postData.getBytes(StandardCharsets.UTF_8));
                    Log.d("AppData", "Sending data: " + postData);

                    // Get the response code
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response (Optional)
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Handle response (Optional)
                        runOnUiThread(this::showAlertDialog1);
                        runOnUiThread(this::resetFields);
                    } else {
                        // Handle error
                        runOnUiThread(() -> {
                            showAlertDialogError("Failed to submit data. Please try again.");
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        showAlertDialogError("An error occurred: " + e.getMessage());
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showAlertDialogError("Encoding error: " + e.getMessage());
        }
    }
    private void showFamilyTree() {
        firstname = FirstNameEditText.getText().toString().trim();
        lastname = LastNameEditText.getText().toString().trim();
        Intent intent = new Intent(this, FamilyTreeActivity.class);
        intent.putExtra("firstName", firstname);
        intent.putExtra("lastName", lastname);
        startActivity(intent);
    }

    @NonNull
    private String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateStr);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty string if parsing fails
        }
    }

    private void showAlertDialogError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message and other dialog properties
        builder.setMessage(message)
                .setTitle("Validation Error!")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean validateFieldsName() {
        // Check if required fields are filled
        boolean isValid = true;

        if (FirstNameEditText.getText().toString().trim().isEmpty()) {
            FirstNameEditText.setError("First name is required");
            isValid = false;
        }

        if (LastNameEditText.getText().toString().trim().isEmpty()) {
            LastNameEditText.setError("Last name is required");
            isValid = false;
        }
        return isValid;
    }
    private boolean validateFields() {
        // Check if required fields are filled
        boolean isValid = true;

        if (FirstNameEditText.getText().toString().trim().isEmpty()) {
            FirstNameEditText.setError("First name is required");
            isValid = false;
        }

        if (LastNameEditText.getText().toString().trim().isEmpty()) {
            LastNameEditText.setError("Last name is required");
            isValid = false;
        }

        if (FatherNameEditText.getText().toString().trim().isEmpty()) {
            FatherNameEditText.setError("Father's name is required");
            isValid = false;
        }

        if (MotherNameEditText.getText().toString().trim().isEmpty()) {
            MotherNameEditText.setError("Mother's name is required");
            isValid = false;
        }

        if (SiblingCheckbox.isChecked() && SiblingLayout1.getVisibility() == View.VISIBLE) {
            if (SiblingSpinner.getSelectedItem() == null || SiblingSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(MainActivity.this, "Please select a number", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                for (int i = 0; i < siblingFirstNameEditTexts.size(); i++) {
                    if (siblingFirstNameEditTexts.get(i).getText().toString().trim().isEmpty() ||
                            siblingLastNameEditTexts.get(i).getText().toString().trim().isEmpty()) {
                        siblingFirstNameEditTexts.get(i).setError("Sibling First and Last Names are required");
                        isValid = false;
                    }
                }
            }
        }

        if (GenderSpinner.getSelectedItem() == null || GenderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(MainActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (BdateEditText.getText().toString().trim().isEmpty()) {
            BdateEditText.setError("Birth date is required");
            isValid = false;
        }

        if (BPlaceEditText.getText().toString().trim().isEmpty()) {
            BPlaceEditText.setError("Birth place is required");
            isValid = false;
        }

        if (MarriageSpinner.getSelectedItem() == null || MarriageSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (MarriageSpinner.getSelectedItem().toString().equals("Married")) {
            if (SpouseEditText.getText().toString().trim().isEmpty()) {
                SpouseEditText.setError("Spouse Name is required");
                isValid = false;
            }
        }
        return isValid;
    }


    @SuppressLint("SetTextI18n")
    private void updateSiblingFields(int numSiblings) {
        // Clear all existing views in the SiblingInfo layout
        SiblingInfo.removeAllViews();
        siblingFirstNameEditTexts.clear();
        siblingLastNameEditTexts.clear();

        for (int i = 1; i <= numSiblings; i++) {
            // Create a new LinearLayout for each sibling's info
            LinearLayout siblingLayout = new LinearLayout(this);
            siblingLayout.setOrientation(LinearLayout.HORIZONTAL);
            siblingLayout.setPadding(0, 10, 0, 10);  // Optional: Add padding for better spacing

            // Create and configure TextView for sibling's full name
            TextView siblingTextView = new TextView(this);
            siblingTextView.setText("Enter Name of Sibling " + i + ":");
            siblingTextView.setTextSize(20);
            siblingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Create and configure Space
            Space space = new Space(this);
            LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                    20, LinearLayout.LayoutParams.MATCH_PARENT);
            space.setLayoutParams(spaceParams);

            // Create and configure EditText for first name
            EditText siblingFirstNameEditText = new EditText(this);
            siblingFirstNameEditText.setHint("First Name");
            siblingFirstNameEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    256, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            // Create and configure Space
            Space space1 = new Space(this);
            LinearLayout.LayoutParams space1Params = new LinearLayout.LayoutParams(
                    150, LinearLayout.LayoutParams.MATCH_PARENT);
            space1.setLayoutParams(space1Params);
            // Create and configure EditText for last name
            EditText siblingLastNameEditText = new EditText(this);
            siblingLastNameEditText.setHint("Last Name");
            siblingLastNameEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    256, LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Store references to the EditTexts
            siblingFirstNameEditTexts.add(siblingFirstNameEditText);
            siblingLastNameEditTexts.add(siblingLastNameEditText);

            // Add views to siblingLayout
            siblingLayout.addView(siblingTextView);
            siblingLayout.addView(space);
            siblingLayout.addView(siblingFirstNameEditText);
            siblingLayout.addView(siblingLastNameEditText);

            // Add siblingLayout to SiblingInfo
            SiblingInfo.addView(siblingLayout);
        }
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message and other dialog properties
        builder.setMessage("Are you sure you want to exit?")
                .setTitle("Alert!!")
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK button
                    System.exit(0);  // Close the application
                    dialog.dismiss(); // Dismiss the dialog
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User clicked Cancel button
                    dialog.dismiss(); // Dismiss the dialog
                });
        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message and other dialog properties
        builder.setMessage("Information Submitted Successfully!")
                .setTitle("Alert!!")
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK button
                    dialog.dismiss(); // Dismiss the dialog
                });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetFields() {
        FirstNameEditText.setText("");
        LastNameEditText.setText("");
        FatherNameEditText.setText("");
        MotherNameEditText.setText("");
        BdateEditText.setText("");
        BPlaceEditText.setText("");
        SpouseEditText.setText("");
        // Reset CheckBoxes
        SiblingCheckbox.setChecked(false);
        // Reset Spinners
        if (GenderSpinner != null || GenderSpinner.getSelectedItemPosition() != 0) {
            GenderSpinner.setSelection(0);
        }
        if (SiblingSpinner.getSelectedItem() != null || SiblingSpinner.getSelectedItemPosition() != 0) {
            SiblingSpinner.setSelection(0);
        }
        if (BloodGroupSpinner.getSelectedItem() != null || BloodGroupSpinner.getSelectedItemPosition() != 0) {
            BloodGroupSpinner.setSelection(0);
        }
        if (MarriageSpinner.getSelectedItem() != null || MarriageSpinner.getSelectedItemPosition() != 0) {
            MarriageSpinner.setSelection(0);
        }
        if(RoleSpinner.getSelectedItem() != null || RoleSpinner.getSelectedItemPosition() != 0) {
            RoleSpinner.setSelection(0);
        }

        // Hide sibling and marriage layouts
        SiblingLayout1.setVisibility(View.GONE);
        SiblingInfo.setVisibility(View.GONE);
        MarriageInfo.setVisibility(View.GONE);

        // Clear sibling information
        siblingFirstNameEditTexts.clear();
        siblingLastNameEditTexts.clear();
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This clears all the data in SharedPreferences
        editor.apply(); // Apply the changes
    }
}