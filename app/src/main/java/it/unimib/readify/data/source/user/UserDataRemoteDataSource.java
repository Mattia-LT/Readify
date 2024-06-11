package it.unimib.readify.data.source.user;

import static it.unimib.readify.util.Constants.FIREBASE_NOTIFICATIONS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_AVATAR_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_BIOGRAPHY_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_EMAILS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_FOLLOWERS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_FOLLOWING_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_GENDER_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_RECOMMENDED_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_TOTAL_NUMBER_OF_BOOKS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_USERNAME_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_USERS_LIST_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_USERS_VISIBILITY_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_WORKS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_WORKS_COMMENTS_FIELD;
import static it.unimib.readify.util.Constants.FOLLOW_ACTION;
import static it.unimib.readify.util.Constants.UNFOLLOW_ACTION;
import static it.unimib.readify.util.Constants.USERNAME_AVAILABLE;
import static it.unimib.readify.util.Constants.USERNAME_ERROR;
import static it.unimib.readify.util.Constants.USERNAME_NOT_AVAILABLE;

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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        DatabaseReference userReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //if snapshot exists, return user data (retrieved from Database)
                    User existingUser = snapshot.getValue(User.class);
                    if(existingUser != null){
                        userResponseCallback.onSuccessFromRemoteDatabase(existingUser);
                    }
                } else {
                    userReference.setValue(user)
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
        DatabaseReference usernameReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_USERNAME_FIELD);

        usernameReference.setValue(user.getUsername())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
        
    }

    //TODO sistemare refactor dopo modifiche di ema
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
        DatabaseReference genderReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_GENDER_FIELD);

        genderReference.setValue(user.getGender())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setVisibility(User user) {
        DatabaseReference visibilityReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_VISIBILITY_FIELD);

        visibilityReference.setValue(user.getVisibility())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setRecommended(User user) {
        DatabaseReference recommendedReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_RECOMMENDED_FIELD);

        recommendedReference.setValue(user.getRecommended())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setAvatar(User user) {
        DatabaseReference avatarReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_AVATAR_FIELD);

        avatarReference.setValue(user.getAvatar())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setBiography(User user) {
        DatabaseReference biographyReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_BIOGRAPHY_FIELD);

        biographyReference.setValue(user.getBiography())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setFollowers(User user) {
        DatabaseReference followersReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_FOLLOWERS_FIELD);

        followersReference.setValue(user.getFollowers())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setFollowing(User user) {
        DatabaseReference followingReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_FOLLOWING_FIELD);

        followingReference.setValue(user.getFollowing())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void setTotalNumberOfBooks(User user) {
        DatabaseReference totalNumberOfBooksReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(user.getIdToken())
                .child(FIREBASE_USERS_TOTAL_NUMBER_OF_BOOKS_FIELD);

        totalNumberOfBooksReference.setValue(user.getTotalNumberOfBooks())
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabaseUser(e.getLocalizedMessage()));
    }

    @Override
    public void fetchNotifications(String idToken) {
        Log.d(TAG, "fetchNotifications");
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken)
                .addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, ArrayList<Notification>> notifications = new HashMap<>();
                        CountDownLatch notificationsTotalTypeCountDownLatch = new CountDownLatch((int)snapshot.getChildrenCount());
                        for (DataSnapshot notificationType: snapshot.getChildren()) {
                            ArrayList<Notification> notificationTypeList = new ArrayList<>();
                            CountDownLatch notificationsTypeCountDownLatch = new CountDownLatch((int)notificationType.getChildrenCount());
                            for (DataSnapshot notificationSnapshot: notificationType.getChildren()) {
                                Notification notification = notificationSnapshot.getValue(Notification.class);
                                if(notification != null) {
                                    CountDownLatch userCountDownLatch = new CountDownLatch(1);
                                    fetchUserFromNotification(notification, userCountDownLatch);
                                    ExecutorService singleNotificationExecutor = Executors.newSingleThreadExecutor();
                                    singleNotificationExecutor.submit(() -> {
                                        try {
                                            userCountDownLatch.await();
                                            notificationTypeList.add(notification);
                                            notificationsTypeCountDownLatch.countDown();
                                            singleNotificationExecutor.shutdown();
                                        } catch (InterruptedException e) {
                                            userResponseCallback.onFailureFetchSingleNotification("Error in " + TAG + " : " + e.getLocalizedMessage());
                                            notificationsTypeCountDownLatch.countDown();
                                        }
                                    });
                                }
                            }
                            ExecutorService notificationTypeExecutor = Executors.newSingleThreadExecutor();
                            notificationTypeExecutor.submit(() -> {
                                try {
                                    notificationsTypeCountDownLatch.await();
                                    notifications.put(notificationType.getKey(), notificationTypeList);
                                    notificationsTotalTypeCountDownLatch.countDown();
                                    notificationTypeExecutor.shutdown();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    userResponseCallback.onFailureFetchNotifications("fetchNotifications error");
                                }
                            });
                        }
                        ExecutorService notificationTotalTypeExecutor = Executors.newSingleThreadExecutor();
                        notificationTotalTypeExecutor.submit(() -> {
                            try {
                                notificationsTotalTypeCountDownLatch.await();
                                userResponseCallback.onSuccessFetchNotifications(notifications);
                                notificationTotalTypeExecutor.shutdown();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                userResponseCallback.onFailureFetchNotifications("fetchNotifications error");
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFetchNotifications("fetchNotifications error");
                    }
                });
    }

    @Override
    public void readNotifications(String idToken, String notificationType) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken).child(notificationType)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ArrayList<Notification> notificationList = new ArrayList<>();
                            for (DataSnapshot notification: snapshot.getChildren()) {
                                Notification singleNotification = notification.getValue(Notification.class);
                                if(singleNotification != null) {
                                    singleNotification.setRead(true);
                                    notificationList.add(singleNotification);
                                }
                            }
                            databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(idToken)
                                    .child(notificationType).setValue(notificationList)
                                    .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessReadNotification(idToken))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureReadNotification(e.getLocalizedMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //todo manage errors
                    }
                });
    }

    @Override
    public void fetchComments(String bookId){
        Log.d(TAG, "Start fetching comments for book with id: " + bookId);
        if (bookId.startsWith("/works/")) {
            bookId = bookId.substring("/works/".length());
        }
        DatabaseReference commentsReference = databaseReference
                .child(FIREBASE_WORKS_COLLECTION)
                .child(bookId)
                .child(FIREBASE_WORKS_COMMENTS_FIELD);

        commentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> comments = new ArrayList<>();
                CountDownLatch commentsCountDownLatch = new CountDownLatch((int) snapshot.getChildrenCount());
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        CountDownLatch userCountDownLatch = new CountDownLatch(1);
                        fetchUserFromComment(comment, userCountDownLatch);
                        ExecutorService singleCommentExecutor = Executors.newSingleThreadExecutor();
                        singleCommentExecutor.submit(() -> {
                            try {
                                userCountDownLatch.await();
                                comments.add(comment);
                                commentsCountDownLatch.countDown();
                                singleCommentExecutor.shutdown();
                            } catch (InterruptedException e) {
                                userResponseCallback.onFailureFetchSingleComment("Error in " + TAG + " : " + e.getLocalizedMessage());
                                commentsCountDownLatch.countDown();
                            }
                        });
                    } else {
                        userResponseCallback.onFailureFetchSingleComment("Error in " + TAG + " : " + "a null comment was found.");
                        commentsCountDownLatch.countDown();
                    }
                }
                ExecutorService commentListExecutor = Executors.newSingleThreadExecutor();
                commentListExecutor.submit(() -> {
                    try {
                        commentsCountDownLatch.await();
                        userResponseCallback.onSuccessFetchCommentsFromRemoteDatabase(comments);
                        commentListExecutor.shutdown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        userResponseCallback.onFailureFetchCommentsFromRemoteDatabase("Error in " + TAG + " : " + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchCommentsFromRemoteDatabase("Error in " + TAG + " : " + error.getMessage());
            }
        });
    }

    @Override
    public void searchUsers(String query, int limit) {
        List<User> userSearchResults = new ArrayList<>();
        DatabaseReference usersCollectionReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION);

        query = query.toLowerCase();
        // query + "\uf8ff" is used to set the end of the range
        Query searchQuery = usersCollectionReference
                .orderByChild(FIREBASE_USERS_USERNAME_FIELD)
                .startAt(query)
                .endAt(query + "\uf8ff");

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int fetchedUserResults = 0;
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    if (fetchedUserResults < limit) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            userSearchResults.add(user);
                            fetchedUserResults++;
                        }
                    } else {
                        //Limit reached, break out of the loop
                        break;
                    }
                }
                userResponseCallback.onSuccessUserSearch(userSearchResults);
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    userResponseCallback.onFailureUserSearch(exception.getMessage());
                }
            }
        });
    }

    @Override
    public void addComment(String commentContent, String bookId, String idToken){
        String finalBookId = bookId.substring("/works/".length());

        DatabaseReference commentsReference = databaseReference
                .child(FIREBASE_WORKS_COLLECTION)
                .child(finalBookId)
                .child(FIREBASE_WORKS_COMMENTS_FIELD);

        DatabaseReference newCommentReference = commentsReference.push();

        String commentId = newCommentReference.getKey();
        long timestamp = new Date().getTime();

        Comment newComment = new Comment(commentId, commentContent, idToken, timestamp);

        newCommentReference.setValue(newComment)
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessAddComment(bookId, newComment))
                .addOnFailureListener(e -> userResponseCallback.onFailureAddComment(e.getLocalizedMessage()));
    }

    @Override
    public void deleteComment(String bookId, Comment deletedComment) {
        String finalBookId = bookId.substring("/works/".length());

        DatabaseReference commentsReference = databaseReference
                .child(FIREBASE_WORKS_COLLECTION)
                .child(finalBookId)
                .child(FIREBASE_WORKS_COMMENTS_FIELD)
                .child(deletedComment.getCommentId());

        commentsReference.removeValue()
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessDeleteComment(bookId, deletedComment))
                .addOnFailureListener(e -> userResponseCallback.onFailureDeleteComment(e.getLocalizedMessage()));
    }

    @Override
    public void fetchFollowers(String idToken) {
        DatabaseReference followersReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD)
                .child(FIREBASE_USERS_USERS_LIST_FIELD);

        followersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FollowUser> followers = new ArrayList<>();
                CountDownLatch followersCountDownLatch = new CountDownLatch((int) snapshot.getChildrenCount());
                for (DataSnapshot singleFollowerSnapshot : snapshot.getChildren()) {
                    FollowUser followUser = singleFollowerSnapshot.getValue(FollowUser.class);
                    if (followUser != null) {
                        CountDownLatch userCountDownLatch = new CountDownLatch(1);
                        fetchUserFromFollowUser(followUser, userCountDownLatch, FIREBASE_USERS_FOLLOWERS_FIELD);
                        ExecutorService singleFollowerExecutor = Executors.newSingleThreadExecutor();
                        singleFollowerExecutor.submit(() -> {
                            try {
                                userCountDownLatch.await();
                                followers.add(followUser);
                                followersCountDownLatch.countDown();
                                singleFollowerExecutor.shutdown();
                            } catch (InterruptedException e) {
                                userResponseCallback.onFailureFetchSingleFollower("Error in " + TAG + " : " + e.getLocalizedMessage());
                                followersCountDownLatch.countDown();
                            }
                        });
                    } else {
                        userResponseCallback.onFailureFetchSingleFollower("Error in " + TAG + " : " + "a null FollowUser was found.");
                        followersCountDownLatch.countDown();
                    }
                }
                ExecutorService followersListExecutor = Executors.newSingleThreadExecutor();
                followersListExecutor.submit(() -> {
                    try {
                        followersCountDownLatch.await();
                        userResponseCallback.onSuccessFetchFollowersFromRemoteDatabase(followers);
                        followersListExecutor.shutdown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        userResponseCallback.onFailureFetchFollowersFromRemoteDatabase("Error in " + TAG + " : " + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchFollowersFromRemoteDatabase("Error in " + TAG + " : " + error.getMessage());
            }
        });
    }

    @Override
    public void fetchFollowings(String idToken) {
        DatabaseReference followingReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_USERS_FOLLOWING_FIELD)
                .child(FIREBASE_USERS_USERS_LIST_FIELD);
        followingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FollowUser> followings = new ArrayList<>();
                CountDownLatch followingsCountDownLatch = new CountDownLatch((int) snapshot.getChildrenCount());
                for (DataSnapshot singleFollowingSnapshot : snapshot.getChildren()) {
                    FollowUser followUser = singleFollowingSnapshot.getValue(FollowUser.class);
                    if (followUser != null) {
                        CountDownLatch userCountDownLatch = new CountDownLatch(1);
                        fetchUserFromFollowUser(followUser, userCountDownLatch, FIREBASE_USERS_FOLLOWING_FIELD);
                        ExecutorService singleFollowingExecutor = Executors.newSingleThreadExecutor();
                        singleFollowingExecutor.submit(() -> {
                            try {
                                userCountDownLatch.await();
                                followings.add(followUser);
                                followingsCountDownLatch.countDown();
                                singleFollowingExecutor.shutdown();
                            } catch (InterruptedException e) {
                                userResponseCallback.onFailureFetchSingleFollowing("Error in " + TAG + " : " + e.getLocalizedMessage());
                                followingsCountDownLatch.countDown();
                            }
                        });
                    } else {
                        userResponseCallback.onFailureFetchSingleFollowing("Error in " + TAG + " : " + "a null FollowUser was found.");
                        followingsCountDownLatch.countDown();
                    }
                }
                ExecutorService followingsListExecutor = Executors.newSingleThreadExecutor();
                followingsListExecutor.submit(() -> {
                    try {
                        followingsCountDownLatch.await();
                        userResponseCallback.onSuccessFetchFollowingFromRemoteDatabase(followings);
                        followingsListExecutor.shutdown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        userResponseCallback.onFailureFetchFollowingFromRemoteDatabase("Error in " + TAG + " : " + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchFollowingFromRemoteDatabase("Error in " + TAG + " : " + error.getMessage());
            }
        });
    }


    @Override
    public void followUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        DatabaseReference loggedUserFollowGroupReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idTokenLoggedUser)
                .child(FIREBASE_USERS_FOLLOWING_FIELD);

        DatabaseReference followedUserFollowGroupReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idTokenFollowedUser)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD);

        FollowUserHandler followUserHandler = new FollowUserHandler(idTokenLoggedUser, idTokenFollowedUser, FOLLOW_ACTION);

        loggedUserFollowGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowGroup followGroupFollowing = snapshot.getValue(FollowGroup.class);

                FollowUser newFollowing = new FollowUser();
                newFollowing.setTimestamp(new Date().getTime());
                newFollowing.setIdToken(idTokenFollowedUser);
                newFollowing.setRead(false);

                if (followGroupFollowing == null) {
                    followGroupFollowing = new FollowGroup();
                    followGroupFollowing.setCounter(0);
                }

                if(followGroupFollowing.getUsers() == null){
                    followGroupFollowing.setUsers(new ArrayList<>());
                }

                followGroupFollowing.getUsers().add(newFollowing);
                followGroupFollowing.setCounter(followGroupFollowing.getUsers().size());
                followUserHandler.onSuccessFetchLoggedUserFollowing(followGroupFollowing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followUserHandler.onFailureFetchLoggedUserFollowing(error.getMessage());
            }
        });

        followedUserFollowGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowGroup followGroupFollowers = snapshot.getValue(FollowGroup.class);

                FollowUser newFollower = new FollowUser();
                newFollower.setTimestamp(new Date().getTime());
                newFollower.setIdToken(idTokenLoggedUser);
                newFollower.setRead(false);

                if (followGroupFollowers == null) {
                    followGroupFollowers = new FollowGroup();
                    followGroupFollowers.setCounter(0);
                }

                if(followGroupFollowers.getUsers() == null){
                    followGroupFollowers.setUsers(new ArrayList<>());
                }

                followGroupFollowers.getUsers().add(newFollower);
                followGroupFollowers.setCounter(followGroupFollowers.getUsers().size());
                followUserHandler.onSuccessFetchFollowedUserFollowers(followGroupFollowers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followUserHandler.onFailureFetchFollowedUserFollowers(error.getMessage());
            }
        });
    }

    @Override
    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        DatabaseReference loggedUserFollowGroupReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idTokenLoggedUser)
                .child(FIREBASE_USERS_FOLLOWING_FIELD);

        DatabaseReference followedUserFollowGroupReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idTokenFollowedUser)
                .child(FIREBASE_USERS_FOLLOWERS_FIELD);

        FollowUserHandler followUserHandler = new FollowUserHandler(idTokenLoggedUser, idTokenFollowedUser, UNFOLLOW_ACTION);

        loggedUserFollowGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowGroup followGroupFollowing = snapshot.getValue(FollowGroup.class);

                // remove following
                if (followGroupFollowing != null && followGroupFollowing.getUsers() != null) {
                    followGroupFollowing.getUsers().removeIf(following -> following.getIdToken().equals(idTokenFollowedUser));
                    followGroupFollowing.setCounter(followGroupFollowing.getUsers().size());
                    followUserHandler.onSuccessFetchLoggedUserFollowing(followGroupFollowing);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followUserHandler.onFailureFetchLoggedUserFollowing(error.getMessage());
            }
        });

        followedUserFollowGroupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowGroup followGroupFollowers = snapshot.getValue(FollowGroup.class);

                // Remove follower
                if (followGroupFollowers != null && followGroupFollowers.getUsers() != null) {
                    followGroupFollowers.getUsers().removeIf(follower -> follower.getIdToken().equals(idTokenLoggedUser));
                    followGroupFollowers.setCounter(followGroupFollowers.getUsers().size());
                    followUserHandler.onSuccessFetchFollowedUserFollowers(followGroupFollowers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                followUserHandler.onFailureFetchFollowedUserFollowers(error.getMessage());
            }
        });
    }

    @Override
    public void endFollowOperation(String idTokenLoggedUser, String idTokenFollowedUser, FollowGroup loggedUserFollowings, FollowGroup followedUserFollowers) {
        DatabaseReference usersReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION);

        String loggedUserPath = idTokenLoggedUser + "/" + FIREBASE_USERS_FOLLOWING_FIELD;
        String followedUserPath = idTokenFollowedUser + "/" + FIREBASE_USERS_FOLLOWERS_FIELD;

        Map<String, Object> updates = new HashMap<>();
        updates.put(loggedUserPath, loggedUserFollowings);
        updates.put(followedUserPath, followedUserFollowers);

        usersReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFollowUser(idTokenLoggedUser, idTokenFollowedUser))
                .addOnFailureListener(e -> userResponseCallback.onFailureFollowUser(TAG + " error in endFollowOperation : " + e.getLocalizedMessage()));
    }

    @Override
    public void endUnfollowOperation(String idTokenLoggedUser, String idTokenFollowedUser, FollowGroup loggedUserFollowings, FollowGroup followedUserFollowers) {
        DatabaseReference usersReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION);

        String loggedUserPath = idTokenLoggedUser + "/" + FIREBASE_USERS_FOLLOWING_FIELD;
        String followedUserPath = idTokenFollowedUser + "/" + FIREBASE_USERS_FOLLOWERS_FIELD;

        Map<String, Object> updates = new HashMap<>();
        updates.put(loggedUserPath, loggedUserFollowings);
        updates.put(followedUserPath, followedUserFollowers);

        usersReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessUnfollowUser(idTokenLoggedUser, idTokenFollowedUser))
                .addOnFailureListener(e -> userResponseCallback.onFailureUnfollowUser(TAG + " error in endUnfollowOperation : " + e.getLocalizedMessage()));
    }

    @Override
    public void fetchOtherUser(String otherUserIdToken) {
        DatabaseReference otherUserReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(otherUserIdToken);

        otherUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User otherUser = snapshot.getValue(User.class);
                            if(otherUser != null){
                                userResponseCallback.onSuccessFetchOtherUser(otherUser);
                            } else {
                                userResponseCallback.onFailureFetchOtherUser(TAG + ": error in fetchOtherUser : this user was null");
                            }
                        } else {
                            userResponseCallback.onFailureFetchOtherUser(TAG + ": error in fetchOtherUser : this snapshot doesn't exist");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFetchOtherUser(TAG + ": error in fetchOtherUser : " + error.getMessage());
                    }
                });
    }

    @Override
    public void refreshLoggedUserData(String idToken){
        DatabaseReference loggedUserReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idToken);

        loggedUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User existingUser = snapshot.getValue(User.class);
                    if(existingUser != null){
                        userResponseCallback.onSuccessFromRemoteDatabase(existingUser);
                    } else {
                        userResponseCallback.onFailureFromRemoteDatabaseUser(TAG + ": error in refreshLoggedUserData : this user was null");
                    }
                } else {
                    userResponseCallback.onFailureFromRemoteDatabaseUser(TAG + ": error in refreshLoggedUserData : this snapshot doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabaseUser(TAG + ": error in fetchOtherUser : " + error.getMessage());
            }
        });
    }


    //TODO sistema notifiche
    @Override
    public void addNotification(String receivingIdToken, String content, String loggedUserIdToken) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(receivingIdToken)
                .child(content)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Notification> notificationsList = new ArrayList<>();
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    Notification notification = datasnapshot.getValue(Notification.class);
                    notificationsList.add(notification);
                }
                notificationsList.add(new Notification(loggedUserIdToken, false, new Date().getTime()));
                databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION).child(receivingIdToken)
                        .child(content).setValue(notificationsList)
                        .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessAddNotification())
                        .addOnFailureListener(e -> userResponseCallback.onFailureAddNotification(e.getLocalizedMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureAddNotification(error.getMessage());
            }
        });
    }

    /*
        1. remove only newFollowers notifications
        2. remove every notification with loggedUserIdToken as idToken
     */
    @Override
    public void removeNotification(String targetIdToken, String content, String loggedUserIdToken) {
        databaseReference.child(FIREBASE_NOTIFICATIONS_COLLECTION)
                .child(targetIdToken)
                .child(content)
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
                                .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessRemoveNotification())
                                .addOnFailureListener(e -> userResponseCallback.onFailureRemoveNotification(e.getLocalizedMessage()));
                    } else {
                        userResponseCallback.onFailureRemoveNotification("no newFollowers notifications detected");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    userResponseCallback.onFailureRemoveNotification(error.getMessage());
                }
            });
    }

    @Override
    public void isUsernameAvailable(String username) {
        DatabaseReference usersReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isUsernameAvailable = true;
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            String existingUsername = userSnapshot.child(FIREBASE_USERS_USERNAME_FIELD).getValue(String.class);
                            if(username.equalsIgnoreCase(existingUsername)){
                                isUsernameAvailable = false;
                                break;
                            }
                        }
                        if (!isUsernameAvailable) {
                            userResponseCallback.onUsernameAvailable(USERNAME_NOT_AVAILABLE);
                        } else {
                            userResponseCallback.onUsernameAvailable(USERNAME_AVAILABLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        userResponseCallback.onUsernameAvailable(USERNAME_ERROR);
                    }
        });
    }

    private void fetchUserFromComment(Comment comment, CountDownLatch userLatch){
        DatabaseReference userReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(comment.getIdToken());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                comment.setUser(user);
                userLatch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFetchSingleComment("Error in " + TAG + " while retrieving user information : " + error.getMessage());
                userLatch.countDown();
            }
        });
    }

    private void fetchUserFromNotification(Notification notification, CountDownLatch userLatch){
        DatabaseReference userReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(notification.getIdToken());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                notification.setUser(user);
                userLatch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               userResponseCallback.onFailureFetchSingleNotification("Error in " + TAG + " while retrieving user information : " + error.getMessage());
               userLatch.countDown();
            }
        });
    }

    private void fetchUserFromFollowUser(FollowUser followUser, CountDownLatch userLatch, String reference){
        DatabaseReference userReference = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(followUser.getIdToken());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                followUser.setUser(user);
                userLatch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(reference.equals(FIREBASE_USERS_FOLLOWERS_FIELD)){
                    userResponseCallback.onFailureFetchSingleFollower("Error in " + TAG + " while retrieving user information : " + error.getMessage());
                } else if(reference.equals(FIREBASE_USERS_FOLLOWING_FIELD)){
                    userResponseCallback.onFailureFetchSingleFollowing("Error in " + TAG + " while retrieving user information : " + error.getMessage());
                }
                userLatch.countDown();
            }
        });
    }

    private class FollowUserHandler{
        private final int RESULT_OK = 1;
        private final int RESULT_FAIL = -1;
        private final int RESULT_UNKNOWN = 0;
        private final String reference;
        private int loggedUserFollowingFetchedResult;
        private int followedUserFollowersFetchedResult;
        private FollowGroup loggedUserFollowings;
        private FollowGroup followedUserFollowers;
        private final String loggedUserIdToken;
        private final String followedUserIdToken;

        private String loggedUserFollowingsErrorMessage;
        private String followedUserFollowersErrorMessage;

        public FollowUserHandler(String loggedUserIdToken, String followedUserIdToken, String reference){
            this.loggedUserFollowingFetchedResult = RESULT_UNKNOWN;
            this.followedUserFollowersFetchedResult = RESULT_UNKNOWN;
            this.loggedUserIdToken = loggedUserIdToken;
            this.followedUserIdToken = followedUserIdToken;
            this.reference = reference;
        }
        public void onSuccessFetchLoggedUserFollowing(FollowGroup loggedUserFollowings){
            this.loggedUserFollowingFetchedResult = RESULT_OK;
            this.loggedUserFollowings = loggedUserFollowings;
            checkData();
        }

        public void onFailureFetchLoggedUserFollowing(String message){
            this.loggedUserFollowingFetchedResult = RESULT_FAIL;
            this.loggedUserFollowingsErrorMessage = message;
            checkData();
        }

        public void onSuccessFetchFollowedUserFollowers(FollowGroup followedUserFollowers){
            this.followedUserFollowersFetchedResult = RESULT_OK;
            this.followedUserFollowers = followedUserFollowers;
            checkData();
        }

        public void onFailureFetchFollowedUserFollowers(String message){
            this.followedUserFollowersFetchedResult = RESULT_FAIL;
            this.followedUserFollowersErrorMessage = message;
            checkData();
        }

        private void checkData(){
            switch (loggedUserFollowingFetchedResult){
                case RESULT_OK:
                    switch (followedUserFollowersFetchedResult){
                        case RESULT_OK:
                            if(reference.equals(FOLLOW_ACTION)){
                                userResponseCallback.onSuccessFetchInfoForFollow(loggedUserIdToken, followedUserIdToken, loggedUserFollowings, followedUserFollowers);
                            } else if(reference.equals(UNFOLLOW_ACTION)){
                                userResponseCallback.onSuccessFetchInfoForUnfollow(loggedUserIdToken, followedUserIdToken, loggedUserFollowings, followedUserFollowers);
                            }
                            break;
                        case RESULT_FAIL:
                            if(reference.equals(FOLLOW_ACTION)){
                                userResponseCallback.onFailureFetchInfoForFollow(followedUserFollowersErrorMessage);
                            } else if(reference.equals(UNFOLLOW_ACTION)){
                                userResponseCallback.onFailureFetchInfoForUnfollow(followedUserFollowersErrorMessage);
                            }
                            break;
                        case RESULT_UNKNOWN:
                            break;
                    }
                    break;
                case RESULT_FAIL:
                    if(reference.equals(FOLLOW_ACTION)){
                        userResponseCallback.onFailureFetchInfoForFollow(loggedUserFollowingsErrorMessage);
                    } else if(reference.equals(UNFOLLOW_ACTION)){
                        userResponseCallback.onFailureFetchInfoForUnfollow(loggedUserFollowingsErrorMessage);
                    }
                    break;
                case RESULT_UNKNOWN:
                    break;
            }
        }
    }
}