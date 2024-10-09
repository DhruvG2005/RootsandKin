package com.example.rootskin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FamilyTreeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FamilyTreeAdapter adapter;
    private List<Family> familyList;
    private RequestQueue queue;
    private TextView tvNoFamilyHistory;
    private ImageButton Back1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        recyclerView = findViewById(R.id.recyclerView);
        tvNoFamilyHistory = findViewById(R.id.tvNoFamilyHistory); // Add reference to the TextView
        Back1 = findViewById(R.id.Back1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        familyList = new ArrayList<>();
        adapter = new FamilyTreeAdapter(familyList);
        recyclerView.setAdapter(adapter);

        Back1.setOnClickListener(v -> {
            Intent intent  = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        queue = Volley.newRequestQueue(this);

        // Fetch person ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String firstName = prefs.getString("firstName", null);
        String lastName = prefs.getString("lastName", null);

        if (firstName != null && lastName != null) {
            fetchPersonId(firstName, lastName);
        } else {
            Toast.makeText(this, "Person ID not found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void fetchPersonId(String firstName, String lastName) {
        String url = "http://192.168.30.219/api/fetch_person_id.php?first_name=" + firstName + "&last_name=" + lastName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("person_id")) {
                            String personId = response.getString("person_id");
                            fetchFamilyTree(personId);
                        } else {
                            Toast.makeText(FamilyTreeActivity.this, "Person not found", Toast.LENGTH_SHORT).show();
                            showNoFamilyHistory();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FamilyTreeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        showNoFamilyHistory();
                    }
                },
                error -> {
//                    Toast.makeText(FamilyTreeActivity.this, "Error fetching person ID", Toast.LENGTH_SHORT).show();
                    showNoFamilyHistory();
                });

        queue.add(jsonObjectRequest);
    }

    private void fetchFamilyTree(String personId) {
        String url = "http://192.168.0.104/api/fetch_family_tree.php?person_id=" + personId;

        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            showNoFamilyHistory(); // Show message if no family history is found
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoFamilyHistory.setVisibility(View.GONE);

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject family = response.getJSONObject(i);
                                String familyName = family.getString("family_name");
                                String role = family.getString("role");

                                JSONArray membersArray = family.getJSONArray("members");
                                List<FamilyMember> members = new ArrayList<>();
                                for (int j = 0; j < membersArray.length(); j++) {
                                    JSONObject member = membersArray.getJSONObject(j);
                                    String firstName = member.getString("first_name");
                                    String lastName = member.getString("last_name");
                                    String gender = member.getString("gender");
                                    String memberRole = member.getString("role");

                                    JSONArray relationshipsArray = member.getJSONArray("relationships");
                                    List<Relationship> relationships = new ArrayList<>();
                                    for (int k = 0; k < relationshipsArray.length(); k++) {
                                        JSONObject relationship = relationshipsArray.getJSONObject(k);
                                        String relationshipType = relationship.getString("relationship_type");

                                        JSONObject relatedPerson = relationship.getJSONObject("related_person");
                                        String relatedFirstName = relatedPerson.getString("first_name");
                                        String relatedLastName = relatedPerson.getString("last_name");
                                        String relatedGender = relatedPerson.getString("gender");

                                        relationships.add(new Relationship(relationshipType, new FamilyMember(relatedFirstName, relatedLastName, relatedGender, "", null)));
                                    }

                                    members.add(new FamilyMember(firstName, lastName, gender, memberRole, relationships));
                                }

                                familyList.add(new Family(familyName, role, members));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FamilyTreeActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        showNoFamilyHistory();
                    }
                },
                error -> {
//                    Toast.makeText(FamilyTreeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    showNoFamilyHistory();
                });

        queue.add(jsonArrayRequest);
    }

    private void showNoFamilyHistory() {
        tvNoFamilyHistory.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
