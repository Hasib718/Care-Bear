package com.hasib.carebear.doctor;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hasib.carebear.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ChamberViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private List<String> aChamberName = new ArrayList<>();
    private List<String> aChamberAddress = new ArrayList<>();
    private Context aContext;

    public RecyclerViewAdapter(Context aContext, List<String> aChamberName, List<String> aChamberAddress) {
        this.aChamberName = aChamberName;
        this.aChamberAddress = aChamberAddress;
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

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        holder.cCamberInfoLayout.getChildAt(1).setBackgroundColor(color);
        holder.cChamberName.setText(aChamberName.get(position));
        holder.cChamberAddress.setText(aChamberAddress.get(position));


        holder.cCamberInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+aChamberName.get(position));

                Toast.makeText(aContext, aChamberName.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return aChamberName.size();
    }

    public class ChamberViewHolder extends RecyclerView.ViewHolder {
        TextView cChamberName;
        TextView cChamberAddress;
        LinearLayout cCamberInfoLayout;

        public ChamberViewHolder(@NonNull View itemView) {
            super(itemView);

            cChamberName = itemView.findViewById(R.id.chamber_name_id);
            cChamberAddress = itemView.findViewById(R.id.chamber_address_id);
            cCamberInfoLayout = itemView.findViewById(R.id.chamber_info_id);
        }
    }
}
