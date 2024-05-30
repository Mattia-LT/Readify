package it.unimib.readify.data.source.collection;

import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_BOOKS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_NAME_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_VISIBILITY_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.readify.R;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLAuthorKeys;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLRatingResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionRemoteDataSource extends BaseCollectionRemoteDataSource{
    private final OLApiService olApiService;
    private final Application application;
    private final DatabaseReference databaseReference;

    public CollectionRemoteDataSource(Application application) {
        this.application = application;
        this.olApiService = ServiceLocator.getInstance(application).getOLApiService();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void fetchWorksForCollections(List<Collection> collections, String reference){
        if(collections != null){
            CountDownLatch collectionsToFetchLatch = new CountDownLatch(collections.size());

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (Collection collection : collections){
                executor.submit(() -> fetchCollectionData(collection, collectionsToFetchLatch));
            }
            executor.shutdown();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                try{
                    Log.d("fetchCollectionData", "Waiting for all collections...");
                    collectionsToFetchLatch.await();
                    Log.d("fetchCollectionData", "All collections fetched, returning collections list");
                    collectionResponseCallback.onSuccessFetchCompleteCollectionsFromRemote(collections, reference);
                    executorService.shutdown();
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    //todo error
                }
            });

        } else {
            //todo error
        }
    }

    @Override
    public void createCollection(String idToken, String collectionName, boolean visible) {
        DatabaseReference collectionsReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(idToken);

        DatabaseReference newCollectionReference = collectionsReference.push();

        String collectionId = newCollectionReference.getKey();
        if(collectionId != null){
            Collection newCollection = new Collection(collectionId, collectionName, visible, new ArrayList<>());
            newCollectionReference.setValue(newCollection);
            collectionResponseCallback.onCreateCollectionResult(newCollection);
        } else {
            //todo error callback
        }

    }

    @Override
    public void deleteCollection(String idToken, Collection collectionToDelete) {
        DatabaseReference collectionReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(idToken)
                .child(collectionToDelete.getCollectionId());

        collectionReference.removeValue()
                .addOnSuccessListener(unused -> collectionResponseCallback.onSuccessDeleteCollectionFromRemote(collectionToDelete))
                .addOnFailureListener(e -> collectionResponseCallback.onFailureDeleteCollectionFromRemote(e.getLocalizedMessage()));
    }

    @Override
    public void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId) {
        DatabaseReference collectionReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(idToken)
                .child(collectionId);

        DatabaseReference booksReference = collectionReference.child(FIREBASE_COLLECTIONS_BOOKS_FIELD);
        DatabaseReference numberOfBooksReference = collectionReference.child(FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD);
        booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ON DATA CHANGED", "ON DATA CHANGED ADD BOOK");
                Set<String> books = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String book = snapshot.getValue(String.class);
                    books.add(book);
                }
                if(book != null){
                    books.add(book.getKey());
                    booksReference.setValue(new ArrayList<>(books));
                    numberOfBooksReference.setValue(books.size());
                    collectionResponseCallback.onSuccessAddBookToCollectionFromRemote(collectionId, book);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO Handle errors
            }
        });
    }

    @Override
    public void removeBookFromCollection(String idToken, String bookId, String collectionId) {
        DatabaseReference collectionReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(idToken)
                .child(collectionId);

        DatabaseReference booksReference = collectionReference.child(FIREBASE_COLLECTIONS_BOOKS_FIELD);
        DatabaseReference numberOfBooksReference = collectionReference.child(FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD);
        booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> books = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String book = snapshot.getValue(String.class);
                    if(book!= null && !book.equals(bookId)){
                        books.add(book);
                    }
                }
                booksReference.setValue(new ArrayList<>(books));
                numberOfBooksReference.setValue(books.size());
                collectionResponseCallback.onSuccessRemoveBookFromCollectionFromRemote(collectionId, bookId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                collectionResponseCallback.onFailureRemoveBookFromCollectionFromRemote(databaseError.getMessage());
            }
        });
    }

    @Override
    public void fetchLoggedUserCollections(String idToken) {
        DatabaseReference collectionsReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(idToken);
        collectionsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Collection> collections = new ArrayList<>();
                for (DataSnapshot collectionSnapshot : snapshot.getChildren()) {
                    Collection collection = collectionSnapshot.getValue(Collection.class);
                    if(collection != null && collection.getBooks() == null){
                        collection.setBooks(new ArrayList<>());
                    }
                    collections.add(collection);
                }
                collectionResponseCallback.onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(collections);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                collectionResponseCallback.onFailureFetchLoggedUserCollectionsFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void fetchOtherUserCollections(String otherUserIdToken) {
        DatabaseReference collectionsReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(otherUserIdToken);
        collectionsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Collection> collections = new ArrayList<>();
                for (DataSnapshot collectionSnapshot : snapshot.getChildren()) {
                    Collection collection = collectionSnapshot.getValue(Collection.class);
                    if(collection != null){
                        if(collection.getBooks() == null){
                            collection.setBooks(new ArrayList<>());
                        }
                        if(collection.isVisible()){
                            collections.add(collection);
                        }
                    } else {
                        //todo error
                    }
                }
                collections.sort(Comparator.comparing(Collection::getName));
                collectionResponseCallback.onSuccessFetchOtherUserCollectionsFromRemoteDatabase(collections);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                collectionResponseCallback.onFailureFetchOtherUserCollectionsFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName) {
        DatabaseReference collectionNameReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(loggedUserIdToken)
                .child(collectionId)
                .child(FIREBASE_COLLECTIONS_NAME_FIELD);

        collectionNameReference.setValue(newCollectionName)
                .addOnSuccessListener(aVoid -> {
                    collectionResponseCallback.onSuccessRenameCollectionFromRemote(collectionId, newCollectionName);
                })
                .addOnFailureListener(e -> {
                    collectionResponseCallback.onFailureRenameCollectionFromRemote(e.getLocalizedMessage());
                });
    }

    @Override
    public void changeCollectionVisibility(String loggedUserIdToken, String collectionId, boolean isCollectionVisible) {
        DatabaseReference collectionVisibilityReference = databaseReference
                .child(FIREBASE_COLLECTIONS_COLLECTION)
                .child(loggedUserIdToken)
                .child(collectionId)
                .child(FIREBASE_COLLECTIONS_VISIBILITY_FIELD);

        collectionVisibilityReference.setValue(isCollectionVisible)
                .addOnSuccessListener(aVoid -> {
                    collectionResponseCallback.onSuccessChangeCollectionVisibilityFromRemote(collectionId, isCollectionVisible);
                })
                .addOnFailureListener(e -> {
                    collectionResponseCallback.onFailureChangeCollectionVisibilityFromRemote(e.getLocalizedMessage());
                });
    }


    public void fetchRatings(OLWorkApiResponse book, CountDownLatch ratingsLatch) {
        olApiService.fetchRating(book.getKey()).enqueue(new Callback<OLRatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLRatingResponse> call, @NonNull Response<OLRatingResponse> response) {
                if(response.isSuccessful()){
                    OLRatingResponse rating = response.body();
                    if(rating != null){
                        book.setRating(rating);
                    } else {
                        //todo errore
                    }
                } else {
                    //todo errore
                }

                ratingsLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call<OLRatingResponse> call, @NonNull Throwable t) {
                //todo errore
                ratingsLatch.countDown();
            }
        });


    }

    private void fetchAuthors(OLWorkApiResponse book, CountDownLatch completeAuthorsLatch){
        List<OLAuthorApiResponse> authors = new ArrayList<>();
        List<OLAuthorKeys> authorsKeys = book.getAuthors();
        if(authorsKeys != null && !authorsKeys.isEmpty()){

            CountDownLatch tempAuthorsLatch = new CountDownLatch(authorsKeys.size());

            for(OLAuthorKeys authorKey : authorsKeys) {
                String key = null;
                if (authorKey.getAuthor() != null) {
                    key = authorKey.getAuthor().getKey();
                } else if (authorKey.getKey() != null) {
                    authorKey.setAuthor(new OLDocs(authorKey.getKey()));
                }
                if(key != null){
                    olApiService.fetchAuthor(key).enqueue(new Callback<OLAuthorApiResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> response) {
                            if(response.isSuccessful()){
                                OLAuthorApiResponse author = response.body();
                                if(author != null){
                                    authors.add(author);
                                } else {
                                    //todo errore
                                }
                            } else {
                                //todo errore
                            }
                            tempAuthorsLatch.countDown();
                        }

                        @Override
                        public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                            //todo errore
                            tempAuthorsLatch.countDown();
                        }
                    });
                } else {
                    tempAuthorsLatch.countDown();
                }

            }


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                try {
                    Log.d("fetchCollectionData", "Waiting for tempAuthors of " + book.getTitle());
                    tempAuthorsLatch.await();
                    Log.d("fetchCollectionData", "All tempAuthors of " + book.getTitle() + " fetched.");
                    book.setAuthorList(authors);
                    completeAuthorsLatch.countDown();
                    executorService.shutdown();
                } catch (InterruptedException e) {
                    // todo error
                    completeAuthorsLatch.countDown();
                }
            });

        } else {
            //todo errore
            completeAuthorsLatch.countDown();
        }

    }

    private void checkBookData(OLWorkApiResponse book){
        if(book.getFirstPublishDate() == null){
            book.setFirstPublishDate("N/A");
        }
        if(book.getDescription() == null){
            String type = "/type/text";
            String value = application.getApplicationContext().getString(R.string.description_not_available);
            book.setDescription(new OLDescription(type, value));
        }
    }


    private void fetchCollectionData(Collection collection, CountDownLatch collectionLatch){
        if (collection.getBooks() == null) {
            collection.setBooks(new ArrayList<>());
        }
        List<String> bookIdList = collection.getBooks();

        if (bookIdList.isEmpty()) {
            collectionLatch.countDown();
            return;
        }
        List<OLWorkApiResponse> fetchedWorks = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch worksToFetchLatch = new CountDownLatch(bookIdList.size());
        Log.d("fetchCollectionData","Started fetching of collection : " + collection.getName() + ", number of books : " + bookIdList.size());
        for(String bookId : bookIdList){
            collection.setWorks(new ArrayList<>());
            olApiService.fetchBook(bookId).enqueue(new Callback<OLWorkApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                    if(response.isSuccessful()){
                        OLWorkApiResponse book = response.body();
                        if(book != null){
                            checkBookData(book);
                            fetchedWorks.add(book);
                            Log.d("fetchCollectionData","just fetched data from : " + collection.getName() + ", book : " + book.getTitle() + ", remaining: " + worksToFetchLatch.getCount());

                        } else {
                            //todo gestire errore
                        }
                    } else {
                        //todo errore
                    }
                    worksToFetchLatch.countDown();
                }

                @Override
                public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                    //todo gestire eventuali errori
                    worksToFetchLatch.countDown();
                }
            });
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try{
                Log.d("fetchCollectionData",collection.getName() + " Waiting for all works...");
                worksToFetchLatch.await();
                Log.d("fetchCollectionData","All works of "+collection.getName()+" completed");

                collection.setWorks(fetchedWorks);
                CountDownLatch ratingsToFetchLatch = new CountDownLatch(fetchedWorks.size());

                for(OLWorkApiResponse book : fetchedWorks){
                    fetchRatings(book, ratingsToFetchLatch);
                }
                Log.d("fetchCollectionData",collection.getName() + " Waiting for all ratings...");
                ratingsToFetchLatch.await();
                Log.d("fetchCollectionData","All ratings of "+collection.getName()+" completed");


                CountDownLatch authorsToFetchLatch = new CountDownLatch(fetchedWorks.size());

                for(OLWorkApiResponse book : fetchedWorks){
                    fetchAuthors(book, authorsToFetchLatch);
                }
                Log.d("fetchCollectionData",collection.getName() + " Waiting for all authors...");
                authorsToFetchLatch.await();
                Log.d("fetchCollectionData","All authors of "+collection.getName()+" completed");
                collectionLatch.countDown();
                executorService.shutdown();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                //todo error
                collectionLatch.countDown();
            }
        });

    }


}
