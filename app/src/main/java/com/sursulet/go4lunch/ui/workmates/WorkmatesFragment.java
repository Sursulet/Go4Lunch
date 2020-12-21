package com.sursulet.go4lunch.ui.workmates;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.OnItemClickListener;
import com.sursulet.go4lunch.ui.chat.ChatActivity;

import java.util.List;

public class WorkmatesFragment extends Fragment implements OnItemClickListener {

    WorkmatesViewModel workmatesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.workmates_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        WorkmatesAdapter adapter = new WorkmatesAdapter(WorkmatesUiModel.DIFF_CALLBACK, this);
        recyclerView.setAdapter(adapter);

        workmatesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);

        workmatesViewModel.getWorkmatesUiModelLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkmatesUiModel>>() {
            @Override
            public void onChanged(List<WorkmatesUiModel> workmatesUiModels) {
                adapter.submitList(workmatesUiModels);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(requireContext(), ChatActivity.class));
    }
}