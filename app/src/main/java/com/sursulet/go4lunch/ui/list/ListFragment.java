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
import android.widget.Toast;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.detail.DetailPlaceActivity;
import com.sursulet.go4lunch.ui.OnItemClickListener;
import com.sursulet.go4lunch.ui.map.MapFragment;

public class ListFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    ListViewModel listViewModel;
    RestaurantAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        listViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class);

        RecyclerView recyclerView = v.findViewById(R.id.list_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new RestaurantAdapter(ListUiModel.DIFF_CALLBACK, this);
        recyclerView.setAdapter(adapter);

        listViewModel.getUiModelMediator().observe(
                getViewLifecycleOwner(),
                listUiModels -> adapter.submitList(listUiModels));

        listViewModel.getSingleLiveEventOpenDetailActivity().observe(this,
                id -> requireActivity().startActivity(DetailPlaceActivity.getStartIntent(requireActivity(), id)));

        listViewModel.getSelectedQuery().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(requireActivity(), "Search Query : " + s, Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onItemClick(int position) {
        ListUiModel place = adapter.getCurrentList().get(position);
        listViewModel.openDetailPlaceActivity(place.getId());
    }
}