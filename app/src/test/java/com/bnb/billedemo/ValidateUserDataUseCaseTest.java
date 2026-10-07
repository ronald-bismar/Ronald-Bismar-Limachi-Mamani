package com.bnb.billedemo;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import com.bnb.billedemo.domain.model.UserData;
import com.bnb.billedemo.domain.usecase.ValidateUserDataUseCase;

import org.junit.Test;

public class ValidateUserDataUseCaseTest {
    private final ValidateUserDataUseCase validator = new ValidateUserDataUseCase();

    @Test
    public void acceptsValidData() {
        assertNull(validator.validate(new UserData("71234567", "412345", "1D")));
    }

    @Test
    public void rejectsPhoneBelowRequiredLength() {
        assertNotNull(validator.validatePhone("7123456"));
    }

    @Test
    public void rejectsPhoneLongerThanEightCharacters() {
        assertNotNull(validator.validatePhone("712345678"));
    }

    @Test
    public void rejectsNonNumericPhone() {
        assertNotNull(validator.validatePhone("7123456A"));
    }

    @Test
    public void rejectsNonNumericCarnet() {
        assertNotNull(validator.validateCarnet("41234A"));
    }

    @Test
    public void rejectsSpecialCharactersInComplement() {
        assertNotNull(validator.validateComplement("1-"));
    }

    @Test
    public void rejectsComplementLongerThanTwoCharacters() {
        assertNotNull(validator.validateComplement("ABC"));
    }
}
