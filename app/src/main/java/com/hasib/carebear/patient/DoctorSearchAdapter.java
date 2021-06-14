package com.hasib.carebear.patient;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.Doctor;

import java.util.List;

public class DoctorSearchAdapter extends RecyclerView.Adapter<DoctorSearchAdapter.viewHolder>{

    private Context context;
    private List<Doctor> doctorList;

    public DoctorSearchAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.doctor_list_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Doctor doctor = doctorList.get(position);
        holder.name.setText(doctor.getFullName());
        holder.specialist.setText(doctor.getSpecialist());
        holder.checkboxinfo.setText(doctor.getCheckBoxInfo());

        holder.doc_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), Doctor_Profile.class);
                i.putExtra("doctor_clicked", doctor);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView specialist;
        TextView checkboxinfo;
        LinearLayout doc_box;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.idDoctorName);
            specialist = itemView.findViewById(R.id.idDocSpecialis);
            checkboxinfo = itemView.findViewById(R.id.idListinfo);
            doc_box = itemView.findViewById(R.id.id_doctor_list_layout);

        }
    }
}
