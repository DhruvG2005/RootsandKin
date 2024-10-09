package com.example.rootskin;

import java.util.List;

public class FamilyMember {
    private String firstName;
    private String lastName;
    private String gender;
    private String role;
    private List<Relationship> relationships;

    public FamilyMember(String firstName, String lastName, String gender, String role, List<Relationship> relationships) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.relationships = relationships;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }
}
