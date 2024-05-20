package it.unimib.readify.data.source.collection;

import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_BOOKS_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_COLLECTION;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_NAME_FIELD;
import static it.unimib.readify.util.Constants.FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.readify.R;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLAuthorKeys;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLRatingResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.util.TestServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionRemoteDataSource extends BaseCollectionRemoteDataSource{
    private final OLApiService olApiService;
    private final Application application;
    private final DatabaseReference databaseReference;

    public CollectionRemoteDataSource(Application application) {
        this.application = application;
        this.olApiService = TestServiceLocator.getInstance(application).getOLApiService();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void fetchWorksForCollections(List<Collection> collections, String reference){
        if(collections != null){
            int collectionsToFetch = collections.size();
            final int[] collectionsFetched = {0};
            for (Collection collection : collections) {
                List<OLWorkApiResponse> fetchedWorks = new ArrayList<>();
                if(collection.getBooks() == null){
                    collection.setBooks(new ArrayList<>());
                }
                List<String> bookIdList = collection.getBooks();
                if(bookIdList.isEmpty()){
                    collectionsFetched[0]++;
                    continue;
                }
                int booksToFetch = bookIdList.size();
                final int[] booksFetched = {0};
                for(String bookId : bookIdList){
                    collection.setWorks(new ArrayList<>());
                    olApiService.fetchBook(bookId).enqueue(new Callback<OLWorkApiResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                            if(response.isSuccessful()){
                                OLWorkApiResponse book = response.body();
                                if(book != null){
                                    checkBookData(book);
                                    fetchAuthors(book);
                                    fetchRatingForWork(book);
                                    fetchedWorks.add(book);
                                    booksFetched[0]++;
                                    if(fetchedWorks.size() == bookIdList.size() && booksFetched[0] == booksToFetch){
                                        collection.setWorks(fetchedWorks);
                                        collectionsFetched[0]++;
                                        if(collectionsFetched[0] == collectionsToFetch){
                                            collectionResponseCallback.onSuccessFetchCompleteCollectionsFromRemote(collections, reference);
                                        }
                                    }
                                } else {
                                    //todo gestire errore
                                }
                            } else {
                                booksFetched[0]++;
                                if(fetchedWorks.size() == bookIdList.size() && booksFetched[0] == booksToFetch){
                                    collection.setWorks(fetchedWorks);
                                    collectionsFetched[0]++;
                                    if(collectionsFetched[0] == collectionsToFetch){
                                        collectionResponseCallback.onSuccessFetchCompleteCollectionsFromRemote(collections, reference);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                            //todo gestire eventuali errori
                            booksFetched[0]++;
                            if(fetchedWorks.size() == bookIdList.size() && booksFetched[0] == booksToFetch){
                                collection.setWorks(fetchedWorks);
                                collectionsFetched[0]++;
                                if(collectionsFetched[0] == collectionsToFetch){
                                    collectionResponseCallback.onSuccessFetchCompleteCollectionsFromRemote(collections, reference);
                                }
                            }
                        }
                    });
                }

            }
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

        collectionReference.removeValue().addOnSuccessListener(unused -> {
            collectionResponseCallback.onSuccessDeleteCollectionFromRemote(collectionToDelete);
        }).addOnFailureListener(e -> {
            collectionResponseCallback.onFailureDeleteCollectionFromRemote(e.getLocalizedMessage());
        });
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
                    //todo create success
                })
                .addOnFailureListener(e -> {
                    //todo create fail
                });
    }














    public void fetchRatingForWork(OLWorkApiResponse workApiResponse) {
        String id = workApiResponse.getKey();
        olApiService.fetchRating(id).enqueue(new Callback<OLRatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLRatingResponse> call, @NonNull Response<OLRatingResponse> response) {
                if(response.isSuccessful()){
                    OLRatingResponse rating = response.body();
                    if(rating != null){
                        workApiResponse.setRating(rating);
                    } else {
                        //todo errore
                    }
                } else {
                    //todo errore
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLRatingResponse> call, @NonNull Throwable t) {
                //todo errore
            }
        });


    }

    private void fetchAuthors(OLWorkApiResponse book){
        List<OLAuthorApiResponse> authors = new ArrayList<>();
        List<OLAuthorKeys> authorsKeys = book.getAuthors();
        if(authorsKeys != null && !authorsKeys.isEmpty()){
            int totalRequests = authorsKeys.size();
            final int[] completedRequests = {0};
            for(OLAuthorKeys authorKey : authorsKeys) {
                String key = null;
                if(authorKey.getAuthor() != null){
                    key = authorKey.getAuthor().getKey();
                } else if (authorKey.getKey() != null){
                    key = authorKey.getKey();
                    authorKey.setAuthor(new OLDocs(key));
                }
                olApiService.fetchAuthor(key).enqueue(new Callback<OLAuthorApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> response) {
                        if(response.isSuccessful()){
                            OLAuthorApiResponse author = response.body();
                            if(author != null){
                                authors.add(author);
                                completedRequests[0]++;
                                if(authors.size() == authorsKeys.size() && completedRequests[0] == totalRequests){
                                    book.setAuthorList(authors);
                                }
                            } else {
                                //todo errore
                            }
                        } else {
                            //todo errore
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                        //todo errore
                        completedRequests[0]++;
                        if(authors.size() == authorsKeys.size() && completedRequests[0] == totalRequests){
                            book.setAuthorList(authors);
                        }
                    }
                });
            }
        }

    }

    private void checkBookData(OLWorkApiResponse book){
        //TODO andrebe spostato negli adapter forse in modo da poter rimuovere la application dal data source, non dovrebbe essere qui
        if(book.getFirstPublishDate() == null){
            book.setFirstPublishDate("N/A");
        }
        if(book.getDescription() == null){
            String type = "/type/text";
            String value = application.getApplicationContext().getString(R.string.description_not_available);
            book.setDescription(new OLDescription(type, value));
        }
    }




}
