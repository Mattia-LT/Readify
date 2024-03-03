package it.unimib.readify.data.source.user;

import static it.unimib.readify.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_USERNAME_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_WORKS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("save user data: signIn case", "User already present in Firebase Realtime Database");
                    //if snapshot exists, return user data (retrieved from Database)
                    userResponseCallback.onSuccessFromRemoteDatabase(snapshot.getValue(User.class));
                } else {
                    Log.d("save user data: signUp case", "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userResponseCallback.onSuccessFromRemoteDatabase(user);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage());
                            }
                        });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
            }
        });
    }

    @Override
    public void saveWorkData(OLWorkApiResponse work) {
        /*
        todo da implementare la corretta classe Comments + vedere se funziona +
         la key corrisponde all'id del libro nel database giusto?
        */
        databaseReference.child(FIREBASE_WORKS_COLLECTION).child(work.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(FIREBASE_USERS_COLLECTION).child(work.getKey()).setValue(work.getComments())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userResponseCallback.onSuccessFromRemoteDatabase(work);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userResponseCallback.onFailureFromRemoteDatabaseWork(e.getLocalizedMessage());
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
            }
        });
    }

    @Override
    public void getUser(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                userResponseCallback.onSuccessFromRemoteDatabase(task.getResult().getValue(User.class));
            }
            else {
                userResponseCallback.onFailureFromRemoteDatabaseUser(task.getException().getLocalizedMessage());
            }
        });
    }

    @Override
    public void getWork(String idBook) {
        /*
            todo mancano gli /
             a logica non dovrebbe essere un problema
         */
        databaseReference.child(FIREBASE_WORKS_COLLECTION).child(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userResponseCallback.onSuccessFromRemoteDatabase(task.getResult().getValue(OLWorkApiResponse.class));
            }
            else {
                userResponseCallback.onFailureFromRemoteDatabaseWork(task.getException().getLocalizedMessage());
            }
        });
    }

    @Override
    public void getUserFromUsername(String username) {
        Log.d("DataSource", "Username: " + username);
        DatabaseReference customReference = databaseReference.child(FIREBASE_USERS_COLLECTION);
        Log.d("DataSource", customReference.toString());
        Query query = customReference.orderByChild(FIREBASE_USERS_USERNAME_FIELD).equalTo(username);
        query.get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               for (DataSnapshot userSnapshot : task.getResult().getChildren()){
                   if(userSnapshot.exists()){
                       Log.d("DataSource","DataSnapshot: " + userSnapshot.getValue());
                       User user = userSnapshot.getValue(User.class);
                       Log.d("Fragment", "UserInfo: " + user.toString());
                       userResponseCallback.onSuccessFromRemoteDatabaseUserFromUsername(user);
                   } else {
                       //todo gestire errore
                       userResponseCallback.onFailureFromRemoteDatabaseUserFromUsername(task.getException().getLocalizedMessage());
                   }
               }
           } else {
               //todo gestire errore
               Exception exception = task.getException();
               if (exception != null) {
                   Log.e("GetUserFromUsername", "Error: " + exception.getMessage());
               }
               userResponseCallback.onFailureFromRemoteDatabaseUserFromUsername(exception.getMessage());
           }
        });
    }

    @Override
    public void searchUsers(String query) {
        //todo possiamo rimuoverlo o passarlo come parametro. per ora lascio qua
        int limit = 10;
        List<User> userSearchResults = new ArrayList<>();
        DatabaseReference customReference = databaseReference.child(FIREBASE_USERS_COLLECTION);
        // query + "\uf8ff" is used to set the end of the range
        Log.d("UserRemoteDataSource", customReference.toString());
        Query searchQuery = customReference.orderByChild(FIREBASE_USERS_USERNAME_FIELD).startAt(query).endAt(query + "\uf8ff");
        Log.d("UserRemoteDataSource", searchQuery.toString());
        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Handle the results
                int count = 0;
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    if (count < limit) {
                        // Assuming you have a User class to represent the user data
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            // Do something with the user, e.g., display it in your UI
                            Log.d("UserSearch", "Found user: " + user);
                            userSearchResults.add(user);
                            count++;
                        }
                    } else {
                        // Limit reached, break out of the loop
                        break;
                    }
                }
                userResponseCallback.onSuccessFromRemoteDatabase(userSearchResults);
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("UserSearch", "Error: " + exception.getMessage());
                }
            }
        });
    }

    @Override
    public void getUserPreferences(String idToken) {
        //todo da implementare
    }

    @Override
    public void saveUserPreferences(String message, String idToken) {
        //todo da implementare
    }

   /*
    @Override
    public void getUserPreferences(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_COUNTRY_OF_INTEREST).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String countryOfInterest = task.getResult().getValue(String.class);
                        sharedPreferencesUtil.writeStringData(
                                SHARED_PREFERENCES_FILE_NAME,
                                SHARED_PREFERENCES_COUNTRY_OF_INTEREST,
                                countryOfInterest);

                        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                child(SHARED_PREFERENCES_TOPICS_OF_INTEREST).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<String> favoriteTopics = new ArrayList<>();
                                            for(DataSnapshot ds : task.getResult().getChildren()) {
                                                String favoriteTopic = ds.getValue(String.class);
                                                favoriteTopics.add(favoriteTopic);
                                            }

                                            if (favoriteTopics.size() > 0) {
                                                Set<String> favoriteNewsSet = new HashSet<>(favoriteTopics);
                                                favoriteNewsSet.addAll(favoriteTopics);

                                                sharedPreferencesUtil.writeStringSetData(
                                                        SHARED_PREFERENCES_FILE_NAME,
                                                        SHARED_PREFERENCES_TOPICS_OF_INTEREST,
                                                        favoriteNewsSet);
                                            }
                                            userResponseCallback.onSuccessFromGettingUserPreferences();
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_COUNTRY_OF_INTEREST).setValue(favoriteCountry);

        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_TOPICS_OF_INTEREST).setValue(new ArrayList<>(favoriteTopics));
    }
    */

}
