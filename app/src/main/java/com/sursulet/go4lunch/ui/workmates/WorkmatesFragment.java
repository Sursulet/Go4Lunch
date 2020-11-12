package com.sursulet.go4lunch.ui.workmates;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference workmatesRef = db.collection("Users");

    private WorkmatesAdapter adapter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.workmates_recyclerview) RecyclerView mRecyclerView;

    public WorkmatesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        RecyclerView recyclerView = view.findViewById(R.id.workmates_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        WorkmatesAdapter adapter = new WorkmatesAdapter(User.DIFF_CALLBACK);
        recyclerView.setAdapter(adapter);

        WorkmatesViewModel workmatesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);
        workmatesViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> workmates) {
                adapter.submitList(workmates);
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
}