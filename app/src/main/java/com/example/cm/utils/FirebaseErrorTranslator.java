package com.example.cm.utils;

import com.example.cm.Constants;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class FirebaseErrorTranslator {
    public static String getErrorMessage(Exception exception) {
        String errorMessage = "";
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            errorMessage = Constants.WEAK_PASSWORD;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            errorMessage = Constants.INVALID_CREDENTIALS;
        } catch (FirebaseAuthUserCollisionException e) {
            errorMessage = Constants.USER_COLLISION;
        } catch (FirebaseAuthInvalidUserException e) {
            errorMessage = Constants.INVALID_CREDENTIALS;
        } catch (Exception e) {
            errorMessage = Constants.DEFAULT_ERROR;
        }
        return errorMessage;
    }
}
