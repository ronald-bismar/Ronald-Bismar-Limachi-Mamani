package com.bnb.billedemo.presentation;

import androidx.lifecycle.ViewModel;

import com.bnb.billedemo.data.FakeBilleRepository;
import com.bnb.billedemo.domain.model.UserData;
import com.bnb.billedemo.domain.repository.BilleRepository;

public final class BilleViewModel extends ViewModel {
    private final BilleRepository repository = new FakeBilleRepository();
    private UserData userData;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void authenticate(BilleRepository.Callback callback) {
        repository.authenticate(userData, callback);
    }
}
