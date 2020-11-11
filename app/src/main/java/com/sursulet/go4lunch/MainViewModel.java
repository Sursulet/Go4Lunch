package com.sursulet.go4lunch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.repository.UserRepository;

public class MainViewModel extends ViewModel {

    //private MutableLiveData<UiModel> mutableLiveData;

    public MainViewModel(UserRepository userRepository) {
    }
}
