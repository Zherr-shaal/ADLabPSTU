package com.asuris19.recorder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asuris19.recorder.R;
import com.asuris19.recorder.RecordsDatabase;
import com.asuris19.recorder.models.RecordModel;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackAdapter.ViewHolder> {

    private RecordsDatabase mDatabase = null;

    public PlaybackAdapter(Context context) {
        mDatabase = new RecordsDatabase(context);
    }

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
        RecordModel record = mDatabase.getItemAt(position);

        long itemDuration = record.getLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.recordName.setText(record.getName());
        holder.recordLength.setText(String.format("%02d:%02d", new Object[]{minutes, seconds}));
        holder.createdDate.setText(new SimpleDateFormat().format(record.getTime()));

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.seekBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView recordName;
        private final TextView recordLength;
        private final TextView createdDate;
        private final ImageButton playButton;
        private final SeekBar seekBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recordName = itemView.findViewById(R.id.record_name);
            recordLength = itemView.findViewById(R.id.record_length);
            createdDate = itemView.findViewById(R.id.created_date);
            playButton = itemView.findViewById(R.id.play_button);
            seekBar = itemView.findViewById(R.id.seekbar);
        }
    }
}
