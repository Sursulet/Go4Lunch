package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesRepository {

    MutableLiveData<List<User>> workmates;

    public LiveData<List<User>> getWorkmates() {
        if(workmates == null) {
            workmates = new MutableLiveData<>();
            List<User> userList = new ArrayList<>();
            userList.add(new User("1", "Noel", null));
            userList.add(new User("2", "Gerald", null));
            userList.add(new User("3", "Jojo", null));
            workmates.setValue(userList);
        }

        return workmates;
    }
}
