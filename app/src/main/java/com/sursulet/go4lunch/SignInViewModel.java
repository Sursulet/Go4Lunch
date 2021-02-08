package com.sursulet.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.UserRepository;

public class SignInViewModel extends ViewModel {

    private final UserRepository userRepository;
    LiveData<User> createdUserLiveData;

    public SignInViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser() {
        userRepository.createUser();
    }
}
