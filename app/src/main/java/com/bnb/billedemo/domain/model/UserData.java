package com.bnb.billedemo.domain.model;

public final class UserData {
    private final String phone;
    private final String carnet;
    private final String complement;

    public UserData(String phone, String carnet, String complement) {
        this.phone = phone;
        this.carnet = carnet;
        this.complement = complement;
    }

    public String getPhone() { return phone; }
    public String getCarnet() { return carnet; }
    public String getComplement() { return complement; }
}
