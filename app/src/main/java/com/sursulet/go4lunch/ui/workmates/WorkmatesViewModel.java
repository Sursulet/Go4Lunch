package com.sursulet.go4lunch.ui.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.WorkmatesRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {

    private WorkmatesRepository workmatesRepository;
    private MutableLiveData<List<User>> usermutableLiveData;

    public WorkmatesViewModel(/*LiveData<List<User>> users*/) {
        //this.usermutableLiveData = users;
    }

    public LiveData<List<User>> getUsers() {
        if(usermutableLiveData == null) {
            usermutableLiveData = new MutableLiveData<>();
            initUsers();
        }

        return usermutableLiveData;
    }

    private void initUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("1", "Noel", null));
        userList.add(new User("2", "Gerald", null));
        userList.add(new User("3", "Jojo", null));
        usermutableLiveData.setValue(userList);
    }
}
