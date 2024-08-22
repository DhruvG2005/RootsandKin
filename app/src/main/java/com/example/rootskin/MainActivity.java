package com.example.rootskin;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import android.widget.Space;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button backButton, submitButton;
    private Spinner GenderSpinner, SiblingSpinner, BloodGroupSpinner;
    private EditText BdateEditText, FirstNameEditText, LastNameEditText, FatherNameEditText, MotherNameEditText, SiblingNameEditText;
    private CheckBox SiblingCheckbox;
    private LinearLayout SiblingLayout1, SiblingInfo;
    private List<EditText> siblingFirstNameEditTexts = new ArrayList<>();
    private List<EditText> siblingLastNameEditTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GenderSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.sibling_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SiblingSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.blood_group_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       BloodGroupSpinner.setAdapter(adapter2);

        // Initially hide the sibling layout
        SiblingLayout1.setVisibility(View.GONE);
        SiblingInfo.setVisibility(View.GONE);

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

        backButton.setOnClickListener(v -> {
            showAlertDialog();
        });

        submitButton.setOnClickListener(v -> {
            // Validate required fields
            if (validateFields()) {
                // Proceed with submission
                sendDataToServer();
                // Uncheck the checkbox after submission
                SiblingCheckbox.setChecked(false);
                // Hide sibling-related views
                SiblingLayout1.setVisibility(View.GONE);
                SiblingInfo.setVisibility(View.GONE);
            }
            else {
                showAlertDialogError("Please fill in the Required Fields!!");
            }
        });
    }

    private void sendDataToServer() {

        String first_name = FirstNameEditText.getText().toString().trim();
        String last_name = LastNameEditText.getText().toString().trim();
        String
        showAlertDialog1();
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

        if (GenderSpinner.getSelectedItem() == null || GenderSpinner.getSelectedItem().toString().trim().isEmpty()) {
            ((TextView) GenderSpinner.getSelectedView()).setError("Gender is required");
            isValid = false;
        }

        if (BdateEditText.getText().toString().trim().isEmpty()) {
            BdateEditText.setError("Birth date is required");
            isValid = false;
        }

        return isValid;
    }

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
}