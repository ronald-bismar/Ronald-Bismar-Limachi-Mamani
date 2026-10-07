package com.bnb.billedemo.data;

import android.os.Handler;
import android.os.Looper;

import com.bnb.billedemo.domain.model.UserData;
import com.bnb.billedemo.domain.repository.BilleRepository;

public final class FakeBilleRepository implements BilleRepository {
    @Override
    public void authenticate(UserData userData, Callback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess();
            }
        }, 1400);
    }
}
