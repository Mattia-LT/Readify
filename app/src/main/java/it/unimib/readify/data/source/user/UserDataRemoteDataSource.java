package it.unimib.readify.data.source.user;

import static it.unimib.readify.util.Constants.FIREBASE_USERS_BIOGRAPHY_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_EMAILS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_NOTIFICATIONS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_AVATAR_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_FOLLOWERS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_FOLLOWING_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_GENDER_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_RECOMMENDED_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_SOCIAL_LINKS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_USERS_LIST_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_VISIBILITY_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_WORKS_COMMENTS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_USERNAME_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_WORKS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.ExternalGroup;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference();
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
                    User existingUser = snapshot.getValue(User.class);
                    if(existingUser != null){
                        userResponseCallback.onSuccessFromRemoteDatabase(existingUser);
                    }
                } else {
                    Log.d("save user data: signUp case", "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                        .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                        .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
            }
        });
    }

    @Override
    public void setUsername(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .orderByChild("username").equalTo(user.getUsername())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userResponseCallback.onUsernameAvailable("notAvailable");
                } else {
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                            .child(FIREBASE_USERS_USERNAME_FIELD).setValue(user.getUsername())
                            .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                            .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                    userResponseCallback.onUsernameAvailable("available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("verifyUsername Firebase error", databaseError.getMessage());
                userResponseCallback.onUsernameAvailable("error");
            }
        });
    }

    //todo modify
    @Override
    public void setEmail(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .orderByChild(FIREBASE_USERS_EMAILS_FIELD).equalTo(user.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //userResponseCallback.onEmailAvailable("notAvailable");
                } else {
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                            .child(FIREBASE_USERS_EMAILS_FIELD).setValue(user.getEmail())
                            .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                            .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                    //userResponseCallback.onEmailAvailable("available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("verifyEmail Firebase error", databaseError.getMessage());
                //userResponseCallback.onEmailAvailable("error");
            }
        });
    }

    @Override
    public void setGender(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_GENDER_FIELD).setValue(user.getGender())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setVisibility(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_VISIBILITY_FIELD).setValue(user.getVisibility())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setRecommended(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_RECOMMENDED_FIELD).setValue(user.getRecommended())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setAvatar(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_AVATAR_FIELD).setValue(user.getAvatar())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setBiography(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_BIOGRAPHY_FIELD).setValue(user.getBiography())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setFollowers(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_FOLLOWERS_FIELD).setValue(user.getFollowers())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setFollowing(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_FOLLOWING_FIELD).setValue(user.getFollowing())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    @Override
    public void setSocialLinks(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken())
                                    .child(FIREBASE_USERS_SOCIAL_LINKS_FIELD).setValue(user.getSocialLinks())
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
                        } else {
                            //todo manage typo
                            userResponseCallback.onFailureFromRemoteDatabaseUser("User doesn't exist yet");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(error.getMessage());
                    }
                });
    }

    /*
        remember that firebase cuts the start "is" from a boolean variable
            es. isRead -> read
     */
    @Override
    public void fetchNotifications(String idToken) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken)
                .addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, ArrayList<Notification>> notifications = new HashMap<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot notificationList: snapshot.getChildren()) {
                                ArrayList<Notification> temp = new ArrayList<>();
                                for (DataSnapshot notification: notificationList.getChildren()) {
                                    String idToken = notification.child("idToken").getValue(String.class);
                                    boolean isRead = Boolean.TRUE.equals(notification.child("read").getValue(Boolean.class));
                                    Object timestampObject = notification.child("timestamp").getValue();
                                    if(timestampObject != null) {
                                        long timeStamp = (long) timestampObject;
                                        temp.add(new Notification(idToken, isRead, timeStamp));
                                    }
                                }
                                notifications.put(notificationList.getKey(), temp);
                            }
                        }
                        userResponseCallback.onSuccessFetchNotifications(notifications);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFetchNotifications("fetchNotifications error");
                    }
                });
    }

    @Override
    //this approach is very efficient IF there aren't many users and usersTokens to match
    public void completeNotificationsFetch(HashMap<String, ArrayList<Notification>> notifications) {
        List<String> usersTokens = new ArrayList<>();
        for (String key: notifications.keySet()) {
            for (Notification notification: Objects.requireNonNull(notifications.get(key))) {
                if(!usersTokens.contains(notification.getIdToken())) {
                    usersTokens.add(notification.getIdToken());
                }
            }
        }
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String idToken: usersTokens) {
                    for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                        User user = datasnapshot.getValue(User.class);
                        if(user != null && idToken.equals(user.getIdToken())) {
                            for (String key: notifications.keySet()) {
                                for (Notification notification: Objects.requireNonNull(notifications.get(key))) {
                                    if(user.getIdToken().equals(notification.getIdToken())
                                            && key.equals("newFollowers")) {
                                        notification.setUsername(user.getUsername());
                                        notification.setAvatar(user.getAvatar());
                                    }
                                }
                            }
                        }
                    }
                }
                userResponseCallback.onSuccessCompleteFetchNotifications(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureCompleteFetchNotifications("completeNotificationsFetch error");
            }
        });
    }

    @Override
    public void setViewedNotificationsListToRead(String idToken, String content, HashMap<String, ArrayList<Notification>> notifications) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken).child(content)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ArrayList<Notification> tempList = new ArrayList<>();
                            for (Notification notification: Objects.requireNonNull(notifications.get(content))) {
                                notification.setRead(true);
                                Notification temp = new Notification(notification);
                                temp.setUsername(null);
                                temp.setAvatar(null);
                                temp.setFollowedByUser(false);
                                tempList.add(temp);
                            }
                            databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken)
                                    .child(content).setValue(tempList)
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFetchNotifications(notifications))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFetchNotifications(e.getLocalizedMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //todo manage errors
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
                userResponseCallback.onFailureFromRemoteDatabaseUser(Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchComments(String bookId){
        Log.d("DataSource", "fetchComments start");
        if (bookId.startsWith("/works/")) {
            bookId = bookId.substring("/works/".length());
        }
        DatabaseReference customReference = databaseReference.child(FIREBASE_WORKS_COLLECTION).child(bookId).child(FIREBASE_WORKS_COMMENTS_FIELD);
        customReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DataSource", "onDataChange ok");
                List<Comment> comments = new ArrayList<>();
                int totalComments = (int) snapshot.getChildrenCount();
                final int[] commentLoaded = {0};
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        Log.d("DataSource", "retrieve user information");
                        fetchUserFromComment(comment, new UserFetchFromCommentCallback(){
                            @Override
                            public void onUserFetched(Comment comment) {
                                Log.d("DataSource", "user information retrieved " + comment.getUser().toString());
                                comments.add(comment);
                                commentLoaded[0]++;

                                if(commentLoaded[0] == totalComments){
                                    userResponseCallback.onSuccessFetchCommentsFromRemoteDatabase(comments);
                                }
                            }

                            @Override
                            public void onUserFetchFailed(Comment comment) {
                                commentLoaded[0]++;
                                Log.d("DataSource", "user information failed");
                                if (commentLoaded[0] == totalComments) {
                                    // All user information retrieved (even if some failed), trigger callback
                                    userResponseCallback.onSuccessFetchCommentsFromRemoteDatabase(comments);
                                }
                            }
                        });
                    } else {
                        commentLoaded[0]++;
                        Log.d("DataSource", "Comment was null");
                    }
                }
                Log.d("DataSource", "comments value " + comments);
                userResponseCallback.onSuccessFetchCommentsFromRemoteDatabase(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DataSource", "comments retrieve failed");
                userResponseCallback.onFailureFetchCommentsFromRemoteDatabase(error.getMessage());
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
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
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
    public void addComment(String content,String bookId, String idToken){
        String finalBookId = bookId.substring("/works/".length());

        DatabaseReference commentsReference = databaseReference
                .child(FIREBASE_WORKS_COLLECTION)
                .child(finalBookId)
                .child(FIREBASE_WORKS_COMMENTS_FIELD);

        DatabaseReference newCommentReference = commentsReference.push();

        String commentId = newCommentReference.getKey();
        long timestamp = new Date().getTime();

        Comment newComment = new Comment(commentId, content, idToken, timestamp);

        newCommentReference.setValue(newComment);
        userResponseCallback.onAddCommentResult(newComment);
    }

    @Override
    public void deleteComment(String bookId, Comment comment) {
        String finalBookId = bookId.substring("/works/".length());
        DatabaseReference commentsReference = databaseReference
                .child(FIREBASE_WORKS_COLLECTION)
                .child(finalBookId)
                .child(FIREBASE_WORKS_COMMENTS_FIELD)
                .child(comment.getCommentId());
        commentsReference.removeValue();
    }

    @Override
    public void fetchFollowers(String idToken) {
        DatabaseReference followersReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD)
                .child(FIREBASE_USERS_USERS_LIST_FIELD);
        followersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ExternalUser> followers = new ArrayList<>();
                int totalFollowers = (int) snapshot.getChildrenCount();
                final int[] followersLoaded = {0};
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    ExternalUser follower = commentSnapshot.getValue(ExternalUser.class);
                    if (follower != null) {
                        fetchUserFromExternalUser(follower, new UserFetchFromExternalUserCallback(){
                            @Override
                            public void onUserFetched(ExternalUser externalUser) {
                                followers.add(externalUser);
                                followersLoaded[0]++;
                                if(followersLoaded[0] == totalFollowers){
                                    userResponseCallback.onSuccessFetchFollowersFromRemoteDatabase(followers);
                                }
                            }

                            @Override
                            public void onUserFetchFailed(ExternalUser externalUser) {
                                followersLoaded[0]++;
                                if (followersLoaded[0] == totalFollowers) {
                                    // All user information retrieved (even if some failed), trigger callback
                                    userResponseCallback.onSuccessFetchFollowersFromRemoteDatabase(followers);
                                }
                            }
                        });
                    } else {
                        followersLoaded[0]++;
                    }
                }
                userResponseCallback.onSuccessFetchFollowersFromRemoteDatabase(followers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchFollowersFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void fetchFollowings(String idToken) {
        DatabaseReference followingReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_USERS_FOLLOWING_FIELD)
                .child(FIREBASE_USERS_USERS_LIST_FIELD);
        followingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ExternalUser> followings = new ArrayList<>();
                int totalFollowings = (int) snapshot.getChildrenCount();
                final int[] followingsLoaded = {0};
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    ExternalUser following = commentSnapshot.getValue(ExternalUser.class);
                    if (following != null) {
                        fetchUserFromExternalUser(following, new UserFetchFromExternalUserCallback(){
                            @Override
                            public void onUserFetched(ExternalUser externalUser) {
                                followings.add(externalUser);
                                followingsLoaded[0]++;
                                if(followingsLoaded[0] == totalFollowings){
                                    userResponseCallback.onSuccessFetchFollowingFromRemoteDatabase(followings);
                                }
                            }

                            @Override
                            public void onUserFetchFailed(ExternalUser externalUser) {
                                followingsLoaded[0]++;
                                if (followingsLoaded[0] == totalFollowings) {
                                    userResponseCallback.onSuccessFetchFollowingFromRemoteDatabase(followings);
                                }
                            }
                        });
                    } else {
                        followingsLoaded[0]++;
                    }
                }
                userResponseCallback.onSuccessFetchFollowingFromRemoteDatabase(followings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchFollowingFromRemoteDatabase(error.getMessage());
            }
        });
    }


    @Override
    public void followUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        Log.d("datasource", "followButtonClick premuto con idtoken: " + idTokenLoggedUser);
        //loggedUser references
        DatabaseReference loggedUserFollowingReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idTokenLoggedUser)
                .child(FIREBASE_USERS_FOLLOWING_FIELD);

        //followedUser references
        DatabaseReference followedUserFollowersReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idTokenFollowedUser)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD);

        final int[] insertionsToDo = {2};
        followedUserFollowersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExternalGroup newExternalGroup = new ExternalGroup();
                ExternalGroup externalGroup = dataSnapshot.getValue(ExternalGroup.class);
                if(externalGroup != null){
                    int counter = externalGroup.getCounter();
                    counter = counter + 1;

                    ExternalUser newFollower = new ExternalUser();
                    newFollower.setTimestamp(new Date().getTime());
                    newFollower.setRead(false);
                    newFollower.setIdToken(idTokenLoggedUser);

                    List<ExternalUser> followersList = externalGroup.getUsers();
                    if(followersList == null){
                        followersList = new ArrayList<>();
                    }
                    followersList.add(newFollower);

                    newExternalGroup.setUsers(followersList);
                    newExternalGroup.setCounter(counter);
                    followedUserFollowersReference.setValue(newExternalGroup);

                    insertionsToDo[0] -= 1;
                    if(insertionsToDo[0] == 0){
                        //TODO passargli qualcosa
                        userResponseCallback.onUserFollowResult();
                        refreshLoggedUserData(idTokenLoggedUser);
                        fetchOtherUser(idTokenFollowedUser);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO Handle errors
            }
        });


        loggedUserFollowingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExternalGroup newExternalGroup = new ExternalGroup();
                ExternalGroup externalGroup = dataSnapshot.getValue(ExternalGroup.class);
                if(externalGroup != null){
                    int counter = externalGroup.getCounter();
                    counter = counter + 1;

                    ExternalUser newFollowing = new ExternalUser();
                    newFollowing.setTimestamp(new Date().getTime());
                    newFollowing.setIdToken(idTokenFollowedUser);

                    List<ExternalUser> followingList = externalGroup.getUsers();
                    if(followingList == null){
                        followingList = new ArrayList<>();
                    }
                    followingList.add(newFollowing);

                    newExternalGroup.setUsers(followingList);
                    newExternalGroup.setCounter(counter);
                    loggedUserFollowingReference.setValue(newExternalGroup);

                    insertionsToDo[0] -= 1;
                    if(insertionsToDo[0] == 0){
                        //TODO passargli qualcosa
                        userResponseCallback.onUserFollowResult();
                        refreshLoggedUserData(idTokenLoggedUser);
                        fetchOtherUser(idTokenFollowedUser);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO Handle errors
            }
        });

    }

    @Override
    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        Log.d("datasource", "unfollowButtonClick premuto con idtoken: " + idTokenLoggedUser);
        //loggedUser references
        DatabaseReference loggedUserFollowingReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idTokenLoggedUser)
                .child(FIREBASE_USERS_FOLLOWING_FIELD);

        //followedUser references
        DatabaseReference followedUserFollowersReference = databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idTokenFollowedUser)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD);

        final int[] deletionsToDo = {2};
        Log.d("unfollow","done");
        followedUserFollowersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExternalGroup newExternalGroup = new ExternalGroup();
                ExternalGroup externalGroup = dataSnapshot.getValue(ExternalGroup.class);
                if(externalGroup != null){
                    int counter = externalGroup.getCounter();
                    counter = counter - 1;

                    List<ExternalUser> followersList = externalGroup.getUsers();
                    if(followersList == null){
                        followersList = new ArrayList<>();
                    }
                    followersList.removeIf(follower -> follower.getIdToken().equals(idTokenLoggedUser));
                    newExternalGroup.setUsers(followersList);
                    newExternalGroup.setCounter(counter);
                    followedUserFollowersReference.setValue(newExternalGroup);

                    deletionsToDo[0] -= 1;
                    if(deletionsToDo[0] == 0){
                        //TODO passargli qualcosa
                        userResponseCallback.onUserUnfollowResult();
                        refreshLoggedUserData(idTokenLoggedUser);
                        fetchOtherUser(idTokenFollowedUser);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO Handle errors
            }
        });


        loggedUserFollowingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExternalGroup newExternalGroup = new ExternalGroup();
                ExternalGroup externalGroup = dataSnapshot.getValue(ExternalGroup.class);
                if(externalGroup != null){
                    int counter = externalGroup.getCounter();
                    counter = counter - 1;

                    List<ExternalUser> followingList = externalGroup.getUsers();
                    if(followingList == null){
                        followingList = new ArrayList<>();
                    }

                    followingList.removeIf(following -> following.getIdToken().equals(idTokenFollowedUser));

                    newExternalGroup.setUsers(followingList);
                    newExternalGroup.setCounter(counter);
                    loggedUserFollowingReference.setValue(newExternalGroup);

                    deletionsToDo[0] -= 1;
                    if(deletionsToDo[0] == 0){
                        //TODO passargli qualcosa
                        userResponseCallback.onUserUnfollowResult();
                        refreshLoggedUserData(idTokenLoggedUser);
                        fetchOtherUser(idTokenFollowedUser);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO Handle errors
            }
        });
    }

    @Override
    public void fetchOtherUser(String otherUserIdToken) {
        databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(otherUserIdToken)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User otherUser = snapshot.getValue(User.class);
                            if(otherUser != null){
                                userResponseCallback.onFetchOtherUserResult(otherUser);
                            } else {
                                //todo error
                            }
                        } else {
                            //todo error
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //todo manage errors
                    }
                });
    }


    private void refreshLoggedUserData(String idToken){
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User existingUser = snapshot.getValue(User.class);
                    if(existingUser != null){
                        userResponseCallback.onSuccessFromRemoteDatabase(existingUser);
                    }
                } else {
                    //Todo manage error
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Todo manage error
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

    @Override
    public void addNotification(String receivingIdToken, String content, String loggedUserIdToken) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(receivingIdToken)
                .child(content).child(loggedUserIdToken)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, ArrayList<Notification>> notifications = new HashMap<>();
                ArrayList<Notification> notificationsList = new ArrayList<>();
                if(snapshot.exists()) {
                    for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                        Notification notification = datasnapshot.getValue(Notification.class);
                        notificationsList.add(notification);
                    }
                }
                notificationsList.add(new Notification(loggedUserIdToken, false, new Date().getTime()));
                databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(receivingIdToken)
                        .child(content).setValue(notificationsList)
                        .addOnSuccessListener(aVoid -> Log.d("addNotification", "success"))
                        .addOnFailureListener(e -> Log.d("addNotification", Objects.requireNonNull(e.getLocalizedMessage())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("addNotification", error.toString());
            }
        });
    }

    /*
        1. remove only newFollowers notifications
        2. remove every notification with loggedUserIdToken as idToken
     */
    @Override
    public void removeNotification(String targetIdToken, String content, String loggedUserIdToken) {
        if(content.equals("newFollowers")) {
            databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(targetIdToken).child(content)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ArrayList<Notification> newFollowersNotifications = new ArrayList<>();
                            for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                                Notification notification = datasnapshot.getValue(Notification.class);
                                if(notification != null && !notification.getIdToken().equals(loggedUserIdToken)) {
                                    newFollowersNotifications.add(notification);
                                }
                            }
                            databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(targetIdToken)
                                    .child(content).setValue(newFollowersNotifications)
                                    .addOnSuccessListener(aVoid -> Log.d("removeNotification", "success"))
                                    .addOnFailureListener(e -> Log.d("removeNotification", Objects.requireNonNull(e.getLocalizedMessage())));
                        } else {
                            Log.d("removeNotification", "no newFollowers notifications detected");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("removeNotification", error.toString());
                    }
                });
        } else {
            Log.d("removeNotification", "incorrect content input");
        }
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

    private void fetchUserFromComment(Comment comment, UserFetchFromCommentCallback callback){
        Log.d("DataSource", "start user retrieve");

        DatabaseReference customReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(comment.getIdToken());
        customReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                comment.setUser(user);
                Log.d("DataSource", "user retrieve OK, " + comment.getUser().toString());
                callback.onUserFetched(comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DataSource", "user retrieve FAIL");
                comment.setUser(null);
                callback.onUserFetched(comment);
            }
        });
    }

    private void fetchUserFromExternalUser(ExternalUser externalUser, UserFetchFromExternalUserCallback callback){
        //todo manage errors

        DatabaseReference userReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(externalUser.getIdToken());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                externalUser.setUser(user);
                callback.onUserFetched(externalUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                externalUser.setUser(null);
                callback.onUserFetched(externalUser);
            }
        });
    }


    private interface UserFetchFromCommentCallback {
        void onUserFetched(Comment comment);
        void onUserFetchFailed(Comment comment);
    }

    private interface UserFetchFromExternalUserCallback {
        void onUserFetched(ExternalUser externalUser);
        void onUserFetchFailed(ExternalUser externalUser);
    }
}