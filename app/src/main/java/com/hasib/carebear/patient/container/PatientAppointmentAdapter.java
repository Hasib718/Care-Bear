package com.hasib.carebear.patient.container;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hasib.carebear.R;
import com.hasib.carebear.databinding.PatientAppointmentListLayoutBinding;
import com.hasib.carebear.patient.DoctorSearchAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatientAppointmentAdapter extends RecyclerView.Adapter<PatientAppointmentAdapter.viewHolder> {

    private Context context;
    private List<Appointment> appointments;

    public PatientAppointmentAdapter(Context context,List<Appointment> appointments)
    {
        this.context=context;
        this.appointments = appointments;
    }
    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        PatientAppointmentListLayoutBinding binding = PatientAppointmentListLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new viewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        holder.binding.idAppointmentTimeinPatient.setText(appointments.get(position).getDoctor().getFullName());
        holder.binding.idDoctorNameinPatient.setText(appointments.get(position).getTimeDate());
        holder.binding.idDoctorAddressinPatient.setText(appointments.get(position).getChamber().getChamberAddress());

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

        public class viewHolder extends RecyclerView.ViewHolder {
        private PatientAppointmentListLayoutBinding binding;

        public viewHolder(@NonNull @NotNull PatientAppointmentListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }
}
