package com.asuris19.recorder.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.asuris19.recorder.R;
import com.asuris19.recorder.RecordsDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordingService extends Service {
    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private RecordsDatabase mDatabase;

    private long mStartingTimeMillis = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new RecordsDatabase(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Boolean shouldStop = intent.getBooleanExtra("stop", false);
        final Boolean shouldSave = intent.getBooleanExtra("save", false);

        if (shouldStop) {
            stopRecording(shouldSave);
            stopSelf();
        } else {
            startRecording();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void setFileNameAndPath() {
        File file;

        do {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            mFileName = currentTime + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/Recordings/" + mFileName;

            file = new File(mFilePath);
        } while (file.exists() && !file.isDirectory());
    }

    private void stopRecording(Boolean shouldSave) {
        mRecorder.stop();
        long mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        mRecorder = null;

        if (shouldSave) {
            try {
                Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();
                mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }
        } else {
            File file = new File(mFilePath);
            file.delete();
        }
    }
}
