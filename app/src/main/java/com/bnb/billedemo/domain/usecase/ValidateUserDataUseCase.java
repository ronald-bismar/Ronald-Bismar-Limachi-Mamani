package com.bnb.billedemo.domain.usecase;

import com.bnb.billedemo.domain.model.UserData;

import java.util.regex.Pattern;

public final class ValidateUserDataUseCase {
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    private static final Pattern COMPLEMENT = Pattern.compile("[A-Za-z0-9]{1,2}");

    public String validatePhone(String value) {
        if (isEmpty(value) || value.length() != 8 || !DIGITS.matcher(value).matches()) {
            return "Ingresa un celular de 8 dígitos";
        }
        return null;
    }

    public String validateCarnet(String value) {
        if (isEmpty(value) || value.length() > 10 || !DIGITS.matcher(value).matches()) {
            return "El carnet debe tener hasta 10 números";
        }
        return null;
    }

    public String validateComplement(String value) {
        if (!isEmpty(value) && !COMPLEMENT.matcher(value).matches()) {
            return "El complemento debe tener 1 o 2 letras o números, sin caracteres especiales";
        }
        return null;
    }

    public String validate(UserData data) {
        String error = validatePhone(data.getPhone());
        if (error != null) return error;
        error = validateCarnet(data.getCarnet());
        if (error != null) return error;
        return validateComplement(data.getComplement());
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
