package com.sursulet.go4lunch.ui.workmates;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.OnItemClickListener;
import com.sursulet.go4lunch.ui.chat.ChatActivity;

public class WorkmatesFragment extends Fragment implements OnItemClickListener {

    WorkmatesViewModel workmatesViewModel;
    WorkmatesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.workmates_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new WorkmatesAdapter(WorkmatesUiModel.DIFF_CALLBACK, this);
        recyclerView.setAdapter(adapter);

        workmatesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(WorkmatesViewModel.class);

        workmatesViewModel.getWorkmatesUiModelLiveData().observe(
                getViewLifecycleOwner(),
                workmatesUiModels -> adapter.submitList(workmatesUiModels));

        workmatesViewModel.getEventOpenChatActivity().observe(
                getViewLifecycleOwner(),
                id -> requireActivity().startActivity(
                        ChatActivity.getStartIntent(requireContext(), id)));

        return view;
    }

    @Override
    public void onItemClick(int position) {
        //TODO : Faut-il récupérer l'id dans le ViewModel ?
        String workmateId = adapter.getCurrentList().get(position).getUid();
        workmatesViewModel.openChatActivity(workmateId);
    }
}