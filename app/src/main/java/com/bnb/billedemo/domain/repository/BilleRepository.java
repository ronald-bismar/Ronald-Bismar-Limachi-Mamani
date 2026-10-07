package com.bnb.billedemo.domain.repository;

import com.bnb.billedemo.domain.model.UserData;

public interface BilleRepository {
    void authenticate(UserData userData, Callback callback);

    interface Callback {
        void onSuccess();
        void onError(String message);
    }
}
