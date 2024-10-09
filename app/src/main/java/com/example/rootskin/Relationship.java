package com.example.rootskin;

public class Relationship {
    private String relationshipType;
    private FamilyMember relatedPerson;

    public Relationship(String relationshipType, FamilyMember relatedPerson) {
        this.relationshipType = relationshipType;
        this.relatedPerson = relatedPerson;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public FamilyMember getRelatedPerson() {
        return relatedPerson;
    }
}
