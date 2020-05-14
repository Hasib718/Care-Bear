package com.hasib.carebear.patient;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasib.carebear.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorSearchAdapter {
    public class viewHolder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        CircleImageView profilePic;
        TextView name;
        RelativeLayout doctorListLayout;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.idDoctorProfilePic);
            name = itemView.findViewById(R.id.iddoctorName);
            doctorListLayout = itemView.findViewById(R.id.id_doctor_list_layout);

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
