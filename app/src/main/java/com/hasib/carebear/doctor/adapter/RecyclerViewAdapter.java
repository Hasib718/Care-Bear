package com.hasib.carebear.doctor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasib.carebear.R;
import com.hasib.carebear.doctor.container.Chamber;
import com.hasib.carebear.doctor.listener.ChamberEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ChamberViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private List<Chamber> aChamberList = new ArrayList<>();
    private Context aContext;

    private ChamberEventListener listener;

    public RecyclerViewAdapter(Context aContext, List<Chamber> aChamberList) {
        this.aChamberList = aChamberList;
        this.aContext = aContext;
    }

    @NonNull
    @Override
    public ChamberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chamber_info, parent, false);
        return new ChamberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamberViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.cChamberName.setText(aChamberList.get(position).getChamberName());
        holder.cChamberFees.setText(aChamberList.get(position).getChamberFess());
        holder.cChamberTime.setText(aChamberList.get(position).getChamberTime());
        holder.cChamberAddress.setText(aChamberList.get(position).getChamberAddress());
//        holder.cChamberActiveDays.setText(aChamberList.get(position).getChamberOpenDays());

        final Chamber chamber = aChamberList.get(position);

        holder.cCamberInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+aChamberList.get(position).getChamberName());

//                Toast.makeText(aContext, aChamberList.get(position).getChamberName(), Toast.LENGTH_SHORT).show();

                listener.onChamberClick(chamber, position);
            }
        });

        holder.cCamberInfoLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onChamberLongClick(chamber, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return aChamberList.size();
    }

    public void setListener(ChamberEventListener listener) {
        this.listener = listener;
    }

    public class ChamberViewHolder extends RecyclerView.ViewHolder {
        TextView cChamberName;
        TextView cChamberFees;
        TextView cChamberTime;
        TextView cChamberAddress;
        TextView cChamberActiveDays;
        LinearLayout cCamberInfoLayout;

        public ChamberViewHolder(@NonNull View itemView) {
            super(itemView);

            cChamberName = itemView.findViewById(R.id.chamber_name_id);
            cChamberFees = itemView.findViewById(R.id.chamber_fee_id);
            cChamberTime = itemView.findViewById(R.id.chamber_time_id);
            cChamberAddress = itemView.findViewById(R.id.chamber_address_id);
            //cChamberActiveDays = itemView.findViewById(R.id.chamber_open_days_id);
            cCamberInfoLayout = itemView.findViewById(R.id.chamber_info_id);
        }
    }
}
