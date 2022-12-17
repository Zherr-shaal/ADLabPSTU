package com.asuris19.recorder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.asuris19.recorder.R;
import com.asuris19.recorder.activities.MainActivity;
import com.asuris19.recorder.adapters.PlaybackAdapter;
import com.asuris19.recorder.databinding.FragmentRecordListBinding;

public class RecordListFragment extends Fragment {

    private FragmentRecordListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordListBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerPlayback.setLayoutManager(linearLayoutManager);
        binding.recyclerPlayback.setAdapter(new PlaybackAdapter(getActivity()));

        binding.buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordFragment recordFragment = new RecordFragment();
                MainActivity activity = (MainActivity) getActivity();
                activity.pushFragment(recordFragment);
            }
        });

        binding.toolbar.setTitle(R.string.app_name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
