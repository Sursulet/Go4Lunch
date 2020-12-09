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
            userList.add(new User("1", "Noel", "https://randomuser.me/api/portraits/men/34.jpg"));
            userList.add(new User("2", "Gerald", "https://randomuser.me/api/portraits/men/88.jpg"));
            userList.add(new User("3", "Jojo", "https://randomuser.me/api/portraits/men/77.jpg"));
            workmates.setValue(userList);
        }

        return workmates;
    }
}
