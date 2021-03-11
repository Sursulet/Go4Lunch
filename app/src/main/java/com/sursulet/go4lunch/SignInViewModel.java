package com.sursulet.go4lunch;

import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.repository.UserRepository;

public class SignInViewModel extends ViewModel {

    private final UserRepository userRepository;

    public SignInViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser() { userRepository.createUser(); }
}
