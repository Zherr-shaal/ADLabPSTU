package com.asuris19.recorder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asuris19.recorder.R;

public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_record, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recordName.setText("Sample record");
        holder.recordLength.setText("99:99");
        holder.createdDate.setText("yesterday");
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView recordName;
        private final TextView recordLength;
        private final TextView createdDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recordName = itemView.findViewById(R.id.record_name);
            recordLength = itemView.findViewById(R.id.record_length);
            createdDate = itemView.findViewById(R.id.created_date);
        }
    }
}
