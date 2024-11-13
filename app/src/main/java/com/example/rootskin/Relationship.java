package com.example.rootskin;

public class Relationship {
    private String person1;           // Name of the first person in the relationship
    private String person2;           // Name of the second person in the relationship
    private String relationshipType;  // Type of the relationship (e.g., "Father", "Brother")
    private String details;           // Additional details about the relationship (e.g., "Cousins", "Half-siblings")

    // Additional details about the relationship (e.g., "Cousins", "Half-siblings")
    // Default no-argument constructor
        public Relationship() {
        }

        // Constructor with parameters (if you still need it)
        public Relationship(String person1, String person2, String relationshipType, String details) {
            this.person1 = person1;
            this.person2 = person2;
            this.relationshipType = relationshipType;
            this.details = details;
        }

        // Getters and Setters
        public String getPerson1() {
            return person1;
        }

        public void setPerson1(String person1) {
            this.person1 = person1;
        }

        public String getPerson2() {
            return person2;
        }

        public void setPerson2(String person2) {
            this.person2 = person2;
        }

        public String getRelationshipType() {
            return relationshipType;
        }

        public void setRelationshipType(String relationshipType) {
            this.relationshipType = relationshipType;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        // toString() method to represent the object as a string
        @Override
        public String toString() {
            return "Relationship{" +
                    "person1='" + person1 + '\'' +
                    ", person2='" + person2 + '\'' +
                    ", relationshipType='" + relationshipType + '\'' +
                    ", details='" + details + '\'' +
                    '}';
        }
    }
