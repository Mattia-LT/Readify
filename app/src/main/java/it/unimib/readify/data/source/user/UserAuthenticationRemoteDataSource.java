package it.unimib.readify.data.source.user;

import static it.unimib.readify.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.readify.util.Constants.INVALID_USER_ERROR;
import static it.unimib.readify.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.readify.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.readify.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import it.unimib.readify.model.User;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource{

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            //todo aggiungo metodo per riempire user con i dati del db
            return new User(firebaseUser.getEmail(), firebaseUser.getDisplayName(), firebaseUser.getUid());
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);
                    userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();

    }

    @Override
    public void registration(String email, String password) {
        //todo gestire gli errori
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(
                            new User( email, firebaseUser.getDisplayName(), firebaseUser.getUid())
                    );
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void login(String email, String password) {
        //todo gestire gli errori
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(
                            new User(email, firebaseUser.getDisplayName(), firebaseUser.getUid())
                    );
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signInWithGoogle(String idToken) {
        //todo gestire errori
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(firebaseUser.getEmail(),
                                        firebaseUser.getDisplayName(),
                                        firebaseUser.getUid()
                                )
                        );
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        return UNEXPECTED_ERROR;
    }


}