package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.controller.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelVerificationServiceTest {
    public static AuthController.TelAndCode VALID_PARAMETER = new AuthController.TelAndCode("13800000000", null);
    public static AuthController.TelAndCode VALID_PARAMETER_CODE = new AuthController.TelAndCode("13800000000", "000000");
    public static AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode(null, null);
    @Test
    public void returnTrueIfValid() {
        Assertions.assertTrue(new TelVerificationService().verifyTelParameter(VALID_PARAMETER));
    }

    @Test
    public void returnFalseIfNoTel() {
        Assertions.assertFalse(new TelVerificationService().verifyTelParameter(EMPTY_TEL));
        Assertions.assertFalse(new TelVerificationService().verifyTelParameter(null));
    }
}
