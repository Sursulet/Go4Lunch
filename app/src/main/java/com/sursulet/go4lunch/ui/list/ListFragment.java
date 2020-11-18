package com.sursulet.go4lunch.ui.list;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;

import java.util.List;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        ListViewModel listViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class);

        RecyclerView recyclerView = v.findViewById(R.id.list_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RestaurantAdapter adapter = new RestaurantAdapter(ListUiModel.DIFF_CALLBACK);
        recyclerView.setAdapter(adapter);

        listViewModel.getListUiModelLiveData().observe(getViewLifecycleOwner(), new Observer<List<ListUiModel>>() {
            @Override
            public void onChanged(List<ListUiModel> listUiModels) {
                adapter.submitList(listUiModels);
            }
        });

        return v;
    }
}