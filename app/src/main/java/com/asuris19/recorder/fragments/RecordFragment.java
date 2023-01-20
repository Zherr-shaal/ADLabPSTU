package com.asuris19.recorder.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;

import androidx.fragment.app.Fragment;

import com.asuris19.recorder.R;
import com.asuris19.recorder.activities.MainActivity;
import com.asuris19.recorder.databinding.FragmentRecordBinding;
import com.asuris19.recorder.services.RecordingService;

public class RecordFragment extends Fragment {
    private FragmentRecordBinding binding;
    private Integer mCounter = 1;
    private Boolean mIsStopped = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.saveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording(true);
                MainActivity activity = (MainActivity) getActivity();
                activity.popFragment();
            }
        });

        binding.removeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording(false);
                MainActivity activity = (MainActivity) getActivity();
                activity.popFragment();
            }
        });

        startRecording();
    }

    @Override
    public void onDestroyView() {
        if (!mIsStopped) {
            stopRecording(false);
        }
        super.onDestroyView();
        binding = null;
    }

    private void startRecording() {
        Intent intent = new Intent(getActivity(), RecordingService.class);

        binding.chronometer.setBase(SystemClock.elapsedRealtime());
        binding.chronometer.start();

        String recordInProgressString = getString(R.string.record_in_progress);
        binding.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (mCounter == 0) {
                    binding.recordingStatusText.setText(recordInProgressString + ".");
                } else if (mCounter == 1) {
                    binding.recordingStatusText.setText(recordInProgressString + "..");
                } else if (mCounter == 2) {
                    binding.recordingStatusText.setText(recordInProgressString + "...");
                    mCounter = -1;
                }

                mCounter++;
            }
        });

        getActivity().startService(intent);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopRecording(Boolean shouldSave) {
        if (mIsStopped) return;

        Intent intent = new Intent(getActivity(), RecordingService.class);

        binding.chronometer.stop();
        binding.chronometer.setBase(SystemClock.elapsedRealtime());

        intent.putExtra("stop", true);
        intent.putExtra("save", shouldSave);

        getActivity().startService(intent);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsStopped = true;
    }
}
