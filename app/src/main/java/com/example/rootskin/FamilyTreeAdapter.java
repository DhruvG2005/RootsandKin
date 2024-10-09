package com.example.rootskin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FamilyTreeAdapter extends RecyclerView.Adapter<FamilyTreeAdapter.FamilyViewHolder> {

    private final List<Family> familyList;

    public FamilyTreeAdapter(List<Family> familyList) {
        this.familyList = familyList;
    }

    @NonNull
    @Override
    public FamilyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family, parent, false);
        return new FamilyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyViewHolder holder, int position) {
        Family family = familyList.get(position);
        holder.familyNameTextView.setText(family.getFamilyName());
        holder.roleTextView.setText(family.getRole());

        // You can bind the family members and relationships here as well
        // using nested RecyclerViews or similar mechanisms.
    }

    @Override
    public int getItemCount() {
        return familyList.size();
    }

    static class FamilyViewHolder extends RecyclerView.ViewHolder {
        TextView familyNameTextView;
        TextView roleTextView;

        FamilyViewHolder(View itemView) {
            super(itemView);
            familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
        }
    }
}
