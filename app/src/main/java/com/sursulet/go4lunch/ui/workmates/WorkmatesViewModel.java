package com.sursulet.go4lunch.ui.workmates;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.WorkmatesRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {

    private final WorkmatesRepository workmatesRepository;

    public WorkmatesViewModel(WorkmatesRepository workmatesRepository) {
        this.workmatesRepository = workmatesRepository;
    }

    public LiveData<List<WorkmatesUiModel>> getWorkmatesUiModelLiveData() {
        return Transformations.map(workmatesRepository.getWorkmates(), new Function<List<User>, List<WorkmatesUiModel>>() {
            @Override
            public List<WorkmatesUiModel> apply(List<User> input) {
                List<WorkmatesUiModel> results = new ArrayList<>();

                for (User user : input) {
                    WorkmatesUiModel workmatesUiModel = new WorkmatesUiModel(
                            user.getUid(),
                            user.getUsername() + " is eating ",
                            user.getAvatarUrl()
                    );
                    results.add(workmatesUiModel);
                }

                return results;
            }
        });
    }
}
