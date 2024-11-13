package com.example.rootskin;

public class FamilyMember {
    private String firstName;
    private String lastName;
    private String role;         // Role (e.g., Father, Mother, Son, Daughter, etc.)
    private String gender;       // Gender (e.g., Male, Female, etc.)
    private String relationship; // Relationship description (e.g., "Paternal Grandfather", "Maternal Grandmother", etc.)
    private int personId;        // Unique ID for the person (from the database)

    // Constructors
    public FamilyMember() {}

//    public FamilyMember(String firstName, String lastName, String role, String gender, String relationship, int personId) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.role = role;
//        this.gender = gender;
//        this.relationship = relationship;
//        this.personId = personId;
//    }
//
//    public FamilyMember(String firstName, String lastName, String role, String gender, int personId) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.role = role;
//        this.gender = gender;
//        this.personId = personId;
//    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return "FamilyMember{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                ", relationship='" + relationship + '\'' +
                ", personId=" + personId +
                '}';
    }
}
