package it.unimib.readify.data.source.user;

import static it.unimib.readify.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.readify.util.Constants.INVALID_USER_ERROR;
import static it.unimib.readify.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.readify.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.readify.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import it.unimib.readify.model.User;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource{

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.d("signUp success", "utente creato con successo");
                    userResponseCallback.onSuccessFromAuthentication
                            (new User(email, firebaseUser.getUid()));
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(
                            new User(email, firebaseUser.getUid())
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
    public void changeEmail(String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.reauthenticate(EmailAuthProvider.getCredential("prova@gmail.com", "password")).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    user.verifyBeforeUpdateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    userResponseCallback.onEmailChanged(task.isSuccessful());
                                    Log.d("source auth", Boolean.toString(task.isSuccessful()));
                                    if(!task.isSuccessful()) {
                                        Log.d("source auth exception", Objects.requireNonNull(task.getException()).toString());
                                    }
                                }
                            });
                } else {
                    Log.d("re-authentication failure", Objects.requireNonNull(task.getException()).toString());
                    userResponseCallback.onEmailChanged(false);
                }
            }
        });
    }

    @Override
    public void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userResponseCallback.onPasswordChanged(task.isSuccessful());
                    }
                });
    }

    @Override
    public void signInWithGoogle(String idToken) {
        //todo da vedere
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        Log.d("USERAUTHREMOTE", "User signed in/up with google");
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(firebaseUser.getEmail(), firebaseUser.getUid()));
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("USERAUTHREMOTE", "signInWithCredential:failure", task.getException());
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
    }

    @Override
    public void logout() {
        //todo da vedere
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);
                    Log.d("USERAUTHREMOTE", "User logged out");
                    userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    private String getErrorMessage(Exception exception) {
        //todo decidere se tenere
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

    //viewmodel -> repo -> getLoggedUser
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            return new User(firebaseUser.getEmail(), firebaseUser.getUid());
        }
    }
}