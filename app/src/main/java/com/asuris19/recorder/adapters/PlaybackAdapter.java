package com.asuris19.recorder.adapters;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asuris19.recorder.AudioRecognizerApi;
import com.asuris19.recorder.R;
import com.asuris19.recorder.RecordsDatabase;
import com.asuris19.recorder.models.RecordModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackAdapter.ViewHolder> {
    private Context mContext;
    private Handler mHandler = new Handler();
    private RecordsDatabase mDatabase = null;
    private ViewHolder mOpenedCard = null;
    private RecordModel mRecordModel = null;
    private MediaPlayer mMediaPlayer = null;

    public PlaybackAdapter(Context context) {
        mContext = context;
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
                if (mOpenedCard != null && mOpenedCard != holder) {
                    mOpenedCard.seekBar.setVisibility(View.GONE);
                    mOpenedCard.isPlaying = false;
                    mOpenedCard.isOnPause = false;
                    mOpenedCard.playButton.setImageResource(R.drawable.baseline_play_arrow_24);
                    mOpenedCard.createdDate.setText(new SimpleDateFormat().format(record.getTime()));
                    stopPlaying();
                }

                mOpenedCard = holder;
                mRecordModel = record;

                if (!holder.isPlaying) {
                    holder.playButton.setImageResource(R.drawable.baseline_pause_24);
                    holder.isPlaying = true;
                    if (holder.isOnPause) {
                        resumePlaying();
                    } else {
                        startPlaying(holder.seekBar.getProgress());
                    }
                } else {
                    holder.playButton.setImageResource(R.drawable.baseline_play_arrow_24);
                    holder.isPlaying = false;
                    pausePlaying();
                }
            }
        });

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        holder.sttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sttResult = AudioRecognizerApi.transcribe(record.getFilePath());
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.sttButton.setVisibility(View.GONE);
                                    holder.sttResult.setVisibility(View.VISIBLE);
                                    holder.sttResult.setText(sttResult);
                                }
                            });
                        } catch (Exception ex) {
                            Log.e("Recorder", ex.getMessage());
                        }
                    }
                }).start();
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
        private final ImageButton sttButton;
        private final TextView sttResult;

        private boolean isPlaying = false;
        private boolean isOnPause = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recordName = itemView.findViewById(R.id.record_name);
            recordLength = itemView.findViewById(R.id.record_length);
            createdDate = itemView.findViewById(R.id.created_date);
            playButton = itemView.findViewById(R.id.play_button);
            seekBar = itemView.findViewById(R.id.seekbar);
            sttButton = itemView.findViewById(R.id.stt_button);
            sttResult = itemView.findViewById(R.id.stt_result);
        }
    }

    private void startPlaying(int progress) {
        if (mOpenedCard == null) return;

        mOpenedCard.playButton.setImageResource(R.drawable.baseline_pause_24);
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(mRecordModel.getFilePath());
            mMediaPlayer.prepare();
            mOpenedCard.seekBar.setMax(mMediaPlayer.getDuration());
            if (progress != mOpenedCard.seekBar.getProgress()) {
                mMediaPlayer.seekTo(progress);
            }

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e("recorder", "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                mOpenedCard.playButton.setImageResource(R.drawable.baseline_play_arrow_24);
                mOpenedCard.seekBar.setProgress(mOpenedCard.seekBar.getMax());
            }
        });

        updateSeekBar();
    }

    private void pausePlaying() {
        if (mOpenedCard == null || mMediaPlayer == null) return;

        mMediaPlayer.pause();
        mHandler.removeCallbacks(mRunnable);
    }

    private void resumePlaying() {
        mMediaPlayer.start();
        updateSeekBar();
    }

    private void stopPlaying() {
        if (mMediaPlayer == null) return;

        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mOpenedCard.isPlaying = false;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mOpenedCard != null) {

                int currentPosition = mMediaPlayer.getCurrentPosition();
                mOpenedCard.seekBar.setProgress(currentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(minutes);

                mOpenedCard.recordLength.setText(String.format("%02d:%02d", minutes, seconds));

                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
}
