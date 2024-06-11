package it.unimib.readify.data.repository.collection;

import static it.unimib.readify.util.Constants.LOGGED_USER;
import static it.unimib.readify.util.Constants.OTHER_USER;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.unimib.readify.data.database.CollectionRoomDatabase;
import it.unimib.readify.data.source.collection.BaseCollectionLocalDataSource;
import it.unimib.readify.data.source.collection.BaseCollectionRemoteDataSource;
import it.unimib.readify.data.source.collection.CollectionLocalDataSource;
import it.unimib.readify.data.source.collection.CollectionRemoteDataSource;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class CollectionRepository implements ICollectionRepository, CollectionResponseCallback{

    private final String TAG = CollectionRepository.class.getSimpleName();

    private final BaseCollectionRemoteDataSource collectionRemoteDataSource;
    private final BaseCollectionLocalDataSource bookLocalDataSource;
    private final MutableLiveData<List<Result>> loggedUserCollectionListLiveData;
    private final MutableLiveData<List<Result>> otherUserCollectionListLiveData;
    private final MutableLiveData<Boolean> allCollectionsDeletedResult;
    private final MutableLiveData<Boolean> addToCollectionResult;
    private final MutableLiveData<Boolean> removeFromCollectionResult;
    private final MutableLiveData<Collection> deletedCollectionResult;


    public static CollectionRepository getInstance(Application application, CollectionRoomDatabase collectionRoomDatabase, SharedPreferencesUtil sharedPreferencesUtil, DataEncryptionUtil dataEncryptionUtil) {
        return new CollectionRepository(
                new CollectionRemoteDataSource(application),
                new CollectionLocalDataSource(collectionRoomDatabase, sharedPreferencesUtil, dataEncryptionUtil)
        );
    }

    public CollectionRepository(BaseCollectionRemoteDataSource collectionRemoteDataSource, BaseCollectionLocalDataSource bookLocalDataSource) {
        loggedUserCollectionListLiveData = new MutableLiveData<>();
        otherUserCollectionListLiveData = new MutableLiveData<>();
        allCollectionsDeletedResult = new MutableLiveData<>();
        addToCollectionResult = new MutableLiveData<>();
        removeFromCollectionResult = new MutableLiveData<>();
        deletedCollectionResult = new MutableLiveData<>();
        this.bookLocalDataSource = bookLocalDataSource;
        this.collectionRemoteDataSource = collectionRemoteDataSource;
        this.bookLocalDataSource.setResponseCallback(this);
        this.collectionRemoteDataSource.setCollectionResponseCallback(this);
    }

    @Override
    public void loadLoggedUserCollectionsFromLocal() {
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void fetchLoggedUserCollections(String idToken) {
        //Fetch logged user collections from remote database on login
        collectionRemoteDataSource.fetchLoggedUserCollections(idToken);
    }

    @Override
    public void fetchOtherUserCollections(String otherUserIdToken) {
        //Fetch other user's collections remotely so that they are always up to date.
        collectionRemoteDataSource.fetchOtherUserCollections(otherUserIdToken);
    }

    @Override
    public void createCollection(String idToken, String collectionName, boolean visible) {
        collectionRemoteDataSource.createCollection(idToken, collectionName, visible);
    }

    @Override
    public void deleteCollection(String idToken, Collection collection) {
        deletedCollectionResult.postValue(null);
        collectionRemoteDataSource.deleteCollection(idToken, collection);
    }

    @Override
    public void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId) {
        addToCollectionResult.postValue(null);
        collectionRemoteDataSource.addBookToCollection(idToken, book, collectionId);
    }

    @Override
    public void removeBookFromCollection(String idToken, String bookId, String collectionId) {
        removeFromCollectionResult.postValue(null);
        collectionRemoteDataSource.removeBookFromCollection(idToken, bookId, collectionId);
    }

    @Override
    public void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName) {
        collectionRemoteDataSource.renameCollection(loggedUserIdToken, collectionId, newCollectionName);
    }

    @Override
    public void changeCollectionVisibility(String loggedUserIdToken, String collectionId, boolean isCollectionVisible) {
        collectionRemoteDataSource.changeCollectionVisibility(loggedUserIdToken,collectionId,isCollectionVisible);
    }

    @Override
    public void emptyLocalDb() {
        bookLocalDataSource.deleteAllCollections();
    }

    @Override
    public void resetLogout() {
        allCollectionsDeletedResult.postValue(null);
    }

    @Override
    public void resetDeleteCollectionResult() {
        deletedCollectionResult.postValue(null);
    }

    @Override
    public MutableLiveData<List<Result>> getLoggedUserCollectionListLiveData() {
        return loggedUserCollectionListLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getOtherUserCollectionListLiveData() {
        return otherUserCollectionListLiveData;
    }

    @Override
    public MutableLiveData<Boolean> getAllCollectionsDeletedResult() {
        return allCollectionsDeletedResult;
    }

    @Override
    public MutableLiveData<Boolean> getAddToCollectionResult() {
        return addToCollectionResult;
    }

    @Override
    public MutableLiveData<Boolean> getRemoveFromCollectionResult() {
        return removeFromCollectionResult;
    }

    @Override
    public MutableLiveData<Collection> getDeleteCollectionResult() {
        return deletedCollectionResult;
    }

    @Override
    public void onSuccessFetchCompleteCollectionsFromRemote(List<Collection> collectionList, String reference) {
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
            List<OLWorkApiResponse> works = collection.getWorks();
            if(works != null){
                works.sort(Comparator.comparing(OLWorkApiResponse::getTitle));
            }
            resultList.add(new Result.CollectionSuccess(collection));
        }
        if(reference.equals(LOGGED_USER)){
            bookLocalDataSource.initLocalCollections(collectionList);
            allCollectionsDeletedResult.postValue(null);
            loggedUserCollectionListLiveData.postValue(resultList);
        } else if(reference.equals(OTHER_USER)){
            otherUserCollectionListLiveData.postValue(resultList);
        }
    }

    @Override
    public void onFailureFetchCompleteCollectionsFromRemote(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessFetchCollectionsFromLocal(List<Collection> collectionList) {
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
            resultList.add(new Result.CollectionSuccess(collection));
        }
        loggedUserCollectionListLiveData.postValue(resultList);
    }

    @Override
    public void onFailureFetchCollectionsFromLocal(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessInsertCollectionFromLocal(List<Collection> collectionList) {
        Log.d(TAG, "Collection created successfully in the local database.");
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
            resultList.add(new Result.CollectionSuccess(collection));
        }
        loggedUserCollectionListLiveData.postValue(resultList);
    }

    @Override
    public void onFailureInsertCollectionFromLocal(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessDeleteCollectionFromLocal(Collection deletedCollection) {
        Log.d(TAG, "Collection deleted successfully from the local database.");
        deletedCollectionResult.postValue(deletedCollection);
        loadLoggedUserCollectionsFromLocal();
    }

    @Override
    public void onFailureDeleteCollectionFromLocal(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    @Override
    public void onSuccessDeleteCollectionFromRemote(Collection deletedCollection) {
        Log.d(TAG, "Collection deleted successfully from the remote database.");
        bookLocalDataSource.deleteCollection(deletedCollection);
    }

    @Override
    public void onFailureDeleteCollectionFromRemote(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    @Override
    public void onSuccessCreateCollectionFromRemote(Collection collection) {
        Log.d(TAG, "Collection created successfully in the remote database.");
        ArrayList<Collection> oneItemCollectionList = new ArrayList<>();
        oneItemCollectionList.add(collection);
        bookLocalDataSource.insertCollectionList(oneItemCollectionList);
    }

    @Override
    public void onFailureCreateCollectionFromRemote(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    @Override
    public void onSuccessAddBookToCollectionFromRemote(String collectionId, OLWorkApiResponse book) {
        Log.d(TAG, "Book " + book.getTitle() + " added to collection " + collectionId + " successfully in the remote database.");
        bookLocalDataSource.addBookToCollection(collectionId, book);
    }

    @Override
    public void onFailureAddBookToCollectionFromRemote(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessAddBookToCollectionFromLocal() {
        Log.d(TAG, "Book added to collection successfully in the local database.");
        addToCollectionResult.postValue(true);
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void onFailureAddBookToCollectionFromLocal(String message) {
        Log.e(TAG, message);
        addToCollectionResult.postValue(false);
    }

    @Override
    public void onSuccessRemoveBookFromCollectionFromLocal() {
        Log.d(TAG, "Book removed from collection successfully in the local database.");
        removeFromCollectionResult.postValue(true);
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void onFailureRemoveBookFromCollectionFromLocal(String message) {
        Log.e(TAG, message);
        removeFromCollectionResult.postValue(false);
    }

    @Override
    public void onSuccessRemoveBookFromCollectionFromRemote(String collectionId, String bookId) {
        Log.d(TAG, "Book removed from collection successfully in the remote database.");
        bookLocalDataSource.removeBookFromCollection(collectionId, bookId);
    }

    @Override
    public void onFailureRemoveBookFromCollectionFromRemote(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessDeleteAllCollectionsFromLocal() {
        Log.d(TAG, "Collections deletion succeeded");
        allCollectionsDeletedResult.postValue(true);
    }

    @Override
    public void onFailureDeleteAllCollectionsFromLocal(String message) {
        Log.e(TAG, message);
        allCollectionsDeletedResult.postValue(false);
    }

    @Override
    public void onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        collectionRemoteDataSource.fetchWorksForCollections(collections, LOGGED_USER);
    }

    @Override
    public void onFailureFetchLoggedUserCollectionsFromRemoteDatabase(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessFetchOtherUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        collectionRemoteDataSource.fetchWorksForCollections(collections, OTHER_USER);
    }

    @Override
    public void onFailureFetchOtherUserCollectionsFromRemoteDatabase(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessChangeCollectionVisibilityFromRemote(String collectionId, boolean isCollectionVisible) {
        Log.d(TAG, "Collection visibility changed successfully in remote database");
        bookLocalDataSource.changeCollectionVisibility(collectionId, isCollectionVisible);
    }

    @Override
    public void onFailureChangeCollectionVisibilityFromRemote(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessChangeCollectionVisibilityFromLocal() {
        Log.d(TAG, "Collection visibility changed successfully in local database");
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void onFailureChangeCollectionVisibilityFromLocal(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessRenameCollectionFromRemote(String collectionId, String newCollectionName) {
        Log.d(TAG, "Collection name changed successfully in remote database");
        bookLocalDataSource.renameCollection(collectionId, newCollectionName);
    }

    @Override
    public void onFailureRenameCollectionFromRemote(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessRenameCollectionFromLocal() {
        Log.d(TAG, "Collection name changed successfully in local database");
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void onFailureRenameCollectionFromLocal(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onFailureFetchRating(String message) {
        //Only a warning, sometimes api doesn't have those information
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchAuthors(String message) {
        //Only a warning, sometimes api doesn't have those information
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchSingleWork(String message) {
        //Only a warning, sometimes api values can change
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchSingleCollection(String message) {
        //Only a warning, sometimes api values can change
        Log.w(TAG, message);
    }
}
