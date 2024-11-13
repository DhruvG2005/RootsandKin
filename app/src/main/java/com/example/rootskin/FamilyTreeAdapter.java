package com.example.rootskin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FamilyTreeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FamilyMember> familyMembers;
    private List<FamilyMember> parents;
    private List<FamilyMember> grandparents;
    private List<Relationship> relationships;

    // Constants for different item types
    private static final int ITEM_TYPE_FAMILY_MEMBER = 0;
    private static final int ITEM_TYPE_PARENT = 1;
    private static final int ITEM_TYPE_GRANDPARENT = 2;
    private static final int ITEM_TYPE_RELATIONSHIP = 3;

    public FamilyTreeAdapter(List<FamilyMember> familyMembers, List<FamilyMember> parents, List<FamilyMember> grandparents, List<Relationship> relationships) {
        this.familyMembers = familyMembers;
        this.parents = parents;
        this.grandparents = grandparents;
        this.relationships = relationships;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine item type based on the data in each list
        if (position < familyMembers.size()) {
            return ITEM_TYPE_FAMILY_MEMBER;
        } else if (position < familyMembers.size() + parents.size()) {
            return ITEM_TYPE_PARENT;
        } else if (position < familyMembers.size() + parents.size() + grandparents.size()) {
            return ITEM_TYPE_GRANDPARENT;
        } else {
            return ITEM_TYPE_RELATIONSHIP;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE_FAMILY_MEMBER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family, parent, false);
                return new FamilyTreeViewHolder(view);
            case ITEM_TYPE_PARENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family, parent, false);
                return new ParentViewHolder(view);
            case ITEM_TYPE_GRANDPARENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family, parent, false);
                return new GrandparentViewHolder(view);
            case ITEM_TYPE_RELATIONSHIP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relationship, parent, false);
                return new RelationshipViewHolder(view);
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case ITEM_TYPE_FAMILY_MEMBER:
                FamilyTreeViewHolder familyHolder = (FamilyTreeViewHolder) holder;
                FamilyMember familyMember = familyMembers.get(position);
                familyHolder.firstName.setText(familyMember.getFirstName());
                familyHolder.lastName.setText(familyMember.getLastName());
                familyHolder.role.setText(familyMember.getRole());
                familyHolder.relationship.setText(familyMember.getRelationship());
                break;

            case ITEM_TYPE_PARENT:
                ParentViewHolder parentHolder = (ParentViewHolder) holder;
                FamilyMember parent = parents.get(position - familyMembers.size());
                parentHolder.firstName.setText(parent.getFirstName());
                parentHolder.lastName.setText(parent.getLastName());
                parentHolder.role.setText(parent.getRole());
                parentHolder.relationship.setText(parent.getRelationship());
                break;

            case ITEM_TYPE_GRANDPARENT:
                GrandparentViewHolder grandparentHolder = (GrandparentViewHolder) holder;
                FamilyMember grandparent = grandparents.get(position - familyMembers.size() - parents.size());
                grandparentHolder.firstName.setText(grandparent.getFirstName());
                grandparentHolder.lastName.setText(grandparent.getLastName());
                grandparentHolder.role.setText(grandparent.getRole());
                grandparentHolder.relationship.setText(grandparent.getRelationship());
                break;

            case ITEM_TYPE_RELATIONSHIP:
                RelationshipViewHolder relationshipHolder = (RelationshipViewHolder) holder;
                Relationship relationship = relationships.get(position - familyMembers.size() - parents.size() - grandparents.size());
                relationshipHolder.details.setText(relationship.getDetails());
                relationshipHolder.person1.setText(relationship.getPerson1());
                relationshipHolder.person2.setText(relationship.getPerson2());
                relationshipHolder.relationshipType.setText(relationship.getRelationshipType());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return familyMembers.size() + parents.size() + grandparents.size() + relationships.size();
    }

    // ViewHolder for Family Members
    public static class FamilyTreeViewHolder extends RecyclerView.ViewHolder {
        public TextView firstName, lastName, role, relationship;

        public FamilyTreeViewHolder(View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            role = itemView.findViewById(R.id.role);
            relationship = itemView.findViewById(R.id.relationship);
        }
    }

    // ViewHolder for Parents
    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        public TextView firstName, lastName, role, relationship;

        public ParentViewHolder(View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            role = itemView.findViewById(R.id.role);
            relationship = itemView.findViewById(R.id.relationship);
        }
    }

    // ViewHolder for Grandparents
    public static class GrandparentViewHolder extends RecyclerView.ViewHolder {
        public TextView firstName, lastName, role, relationship;

        public GrandparentViewHolder(View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            role = itemView.findViewById(R.id.role);
            relationship = itemView.findViewById(R.id.relationship);
        }
    }

    // ViewHolder for Relationships
    public static class RelationshipViewHolder extends RecyclerView.ViewHolder {
        public TextView person1, person2, relationshipType, details;

        public RelationshipViewHolder(View itemView) {
            super(itemView);
            person1 = itemView.findViewById(R.id.person_1);
            person2 = itemView.findViewById(R.id.person_2);
            relationshipType = itemView.findViewById(R.id.relationship_type);
            details = itemView.findViewById(R.id.details);
        }
    }
}
