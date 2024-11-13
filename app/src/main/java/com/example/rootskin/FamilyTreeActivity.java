package com.example.rootskin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FamilyTreeActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private List<FamilyMember> familyMembers;
    private List<FamilyMember> parents;
    private List<FamilyMember> grandparents;
    private List<Relationship> relationships;
    private String firstName, lastName, firstname, lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        tableLayout = findViewById(R.id.tableLayout);
        familyMembers = new ArrayList<>();
        parents = new ArrayList<>();
        grandparents = new ArrayList<>();
        relationships = new ArrayList<>();

        // Get the Intent that started this activity
        Intent intent = getIntent();

        // Retrieve the first name and last name from the intent
        firstname = intent.getStringExtra("firstName");
        lastname = intent.getStringExtra("lastName");

        // Fetch family tree data based on first and last name
        fetchFamilyTreeData(firstname, lastname);
    }

    private void fetchFamilyTreeData(String firstName, String lastName) {
        String url = "http://192.168.155.37/fetch_family_tree.php?first_name=" + firstName + "&last_name=" + lastName;
        Log.d("FamilyTreeActivity", "Fetching family tree for: " + firstName + " " + lastName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Log the response for debugging
                            Log.d("FamilyTreeActivity", "Response: " + response.toString());

                            // Parse the family members array
                            JSONArray familyMembersArray = response.optJSONArray("family_members");
                            if (familyMembersArray != null) {
                                for (int i = 0; i < familyMembersArray.length(); i++) {
                                    JSONObject familyMemberObj = familyMembersArray.getJSONObject(i);
                                    FamilyMember familyMember = new FamilyMember();
                                    familyMember.setFirstName(familyMemberObj.getString("first_name"));
                                    familyMember.setLastName(familyMemberObj.getString("last_name"));
                                    familyMember.setRole(familyMemberObj.getString("role"));
                                    familyMember.setGender(familyMemberObj.getString("gender"));
                                    familyMember.setPersonId(familyMemberObj.getInt("person_id"));
                                    familyMembers.add(familyMember);
                                }
                            } else {
                                Log.w("FamilyTreeActivity", "'family_members' key is missing or is null in the response.");
                            }

                            // Parse the parents array
                            JSONArray parentsArray = response.optJSONArray("parents");
                            if (parentsArray != null) {
                                for (int i = 0; i < parentsArray.length(); i++) {
                                    JSONObject parentObj = parentsArray.getJSONObject(i);
                                    FamilyMember parent = new FamilyMember();
                                    parent.setFirstName(parentObj.getString("first_name"));
                                    parent.setLastName(parentObj.getString("last_name"));
                                    parent.setRelationship(parentObj.getString("relationship"));
                                    parents.add(parent);
                                }
                            } else {
                                Log.w("FamilyTreeActivity", "'parents' key is missing or is null in the response.");
                            }

                            // Parse the grandparents array
                            JSONArray grandparentsArray = response.optJSONArray("grandparents");
                            if (grandparentsArray != null) {
                                for (int i = 0; i < grandparentsArray.length(); i++) {
                                    JSONObject grandparentObj = grandparentsArray.getJSONObject(i);
                                    FamilyMember grandparent = new FamilyMember();
                                    grandparent.setFirstName(grandparentObj.getString("first_name"));
                                    grandparent.setLastName(grandparentObj.getString("last_name"));
                                    grandparent.setRelationship(grandparentObj.getString("relationship"));
                                    grandparents.add(grandparent);
                                }
                            } else {
                                Log.w("FamilyTreeActivity", "'grandparents' key is missing or is null in the response.");
                            }

                            // Parse relationships
                            JSONArray relationshipsArray = response.optJSONArray("relationships");
                            if (relationshipsArray != null) {
                                for (int i = 0; i < relationshipsArray.length(); i++) {
                                    JSONObject relationshipObj = relationshipsArray.getJSONObject(i);
                                    Relationship relationship = new Relationship();
                                    relationship.setPerson1(relationshipObj.getString("person_1"));
                                    relationship.setPerson2(relationshipObj.getString("person_2"));
                                    relationship.setRelationshipType(relationshipObj.getString("relationship_type"));
                                    relationship.setDetails(relationshipObj.getString("details"));
                                    relationships.add(relationship);
                                }
                            } else {
                                Log.w("FamilyTreeActivity", "'relationships' key is missing or is null in the response.");
                            }

                            // Dynamically add rows to the table
                            addRowsToTable();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FamilyTreeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Log.e("Volley", "Error: " + error.getMessage());
                    Toast.makeText(FamilyTreeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        // Add request to Volley request queue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addRowsToTable() {
        // Add Family Members
        for (FamilyMember familyMember : familyMembers) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView(familyMember.getFirstName() + " " + familyMember.getLastName(), Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            tableLayout.addView(row);
        }

        // Add Parents
        for (FamilyMember parent : parents) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView(parent.getFirstName() + " " + parent.getLastName(), Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            tableLayout.addView(row);
        }

        // Add Grandparents
        for (FamilyMember grandparent : grandparents) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            row.addView(createTextView(grandparent.getFirstName() + " " + grandparent.getLastName(), Gravity.CENTER, 1));
            row.addView(createTextView("", Gravity.CENTER, 1));
            tableLayout.addView(row);
        }

        // Add Relationships
        for (Relationship relationship : relationships) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView("", Gravity.CENTER, 1)); // Family Members
            row.addView(createTextView("", Gravity.CENTER, 1)); // Parents
            row.addView(createTextView("", Gravity.CENTER, 1)); // Grandparents
            row.addView(createTextView(relationship.getPerson1() + " & " + relationship.getPerson2() + ": " + relationship.getRelationshipType(), Gravity.CENTER, 1)); // Relationships
            tableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, int gravity, int weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextSize(14);
        textView.setGravity(gravity);

        // Set weight for equal distribution of column space
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);

        return textView;
    }

}
