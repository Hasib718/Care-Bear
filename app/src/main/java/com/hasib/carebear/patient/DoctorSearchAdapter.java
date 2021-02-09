package com.hasib.carebear.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.UserDetails;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorSearchAdapter extends RecyclerView.Adapter<DoctorSearchAdapter.viewHolder>{

    private Context context;
    private List<UserDetails> doctorList;

    public DoctorSearchAdapter(Context context, List<UserDetails> doctorList) {
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
        UserDetails userDetails = doctorList.get(position);
        holder.name.setText(userDetails.getFullName());

        // TODO: 6/3/20 bind other details of doctor to the UI elements

    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView specialist;
        TextView checkboxinfo;
        // TODO: 6/3/20 declare other thing to show while searching doctor


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.iddoctorName);
            // TODO: 6/3/20  find the things declared in the todo above this

        }
    }
}
