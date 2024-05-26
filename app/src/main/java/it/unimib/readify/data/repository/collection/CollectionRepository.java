package it.unimib.readify.data.repository.collection;

import static it.unimib.readify.util.Constants.LOGGED_USER;
import static it.unimib.readify.util.Constants.OTHER_USER;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.database.BookRoomDatabase;
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

    private final BaseCollectionRemoteDataSource collectionRemoteDataSource;
    private final BaseCollectionLocalDataSource bookLocalDataSource;
    private final MutableLiveData<List<Result>> loggedUserCollectionListLiveData;
    private final MutableLiveData<List<Result>> otherUserCollectionListLiveData;
    private final MutableLiveData<Boolean> allCollectionsDeletedResult;

    public static CollectionRepository getInstance(Application application, BookRoomDatabase bookRoomDatabase, SharedPreferencesUtil sharedPreferencesUtil, DataEncryptionUtil dataEncryptionUtil) {
        return new CollectionRepository(
                new CollectionRemoteDataSource(application),
                new CollectionLocalDataSource(bookRoomDatabase, sharedPreferencesUtil, dataEncryptionUtil)
        );
    }

    public CollectionRepository(BaseCollectionRemoteDataSource collectionRemoteDataSource, BaseCollectionLocalDataSource bookLocalDataSource) {
        loggedUserCollectionListLiveData = new MutableLiveData<>();
        otherUserCollectionListLiveData = new MutableLiveData<>();
        allCollectionsDeletedResult = new MutableLiveData<>();
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
        //Da chiamare solo al primo loading
        collectionRemoteDataSource.fetchLoggedUserCollections(idToken);
    }

    @Override
    public void fetchOtherUserCollections(String otherUserIdToken) {
        //Sempre da remoto
        collectionRemoteDataSource.fetchOtherUserCollections(otherUserIdToken);
    }

    @Override
    public void createCollection(String idToken, String collectionName, boolean visible) {
        collectionRemoteDataSource.createCollection(idToken, collectionName, visible);
    }

    @Override
    public void deleteCollection(String idToken, Collection collection) {
        collectionRemoteDataSource.deleteCollection(idToken, collection);
    }

    @Override
    public void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId) {
        collectionRemoteDataSource.addBookToCollection(idToken, book, collectionId);
    }

    @Override
    public void removeBookFromCollection(String idToken, String bookId, String collectionId) {
        collectionRemoteDataSource.removeBookFromCollection(idToken, bookId, collectionId);
    }

    @Override
    public void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName) {
        collectionRemoteDataSource.renameCollection(loggedUserIdToken, collectionId, newCollectionName);
    }

    @Override
    public void emptyLocalDb() {
        bookLocalDataSource.deleteAllCollections();
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
    public void onSuccessFetchCompleteCollectionsFromRemote(List<Collection> collectionList, String reference) {
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
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
    public void onSuccessFetchCollectionsFromLocal(List<Collection> collectionList) {
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
            resultList.add(new Result.CollectionSuccess(collection));
        }
        loggedUserCollectionListLiveData.postValue(resultList);
        //todo mettere on failure, magari aggiungendo un fetch da remoto se per qualche motivo non funziona?
    }

    @Override
    public void onSuccessInsertCollectionFromLocal(List<Collection> collectionList) {
        Log.d("CollectionRepository", "Collection created successfully.");
    }

    @Override
    public void onSuccessDeleteCollectionFromLocal(Collection deletedCollection) {
        //todo far vedere all utente se va bene o meno
        Log.e("TUTTO OK", "COLLEZIONE CANCELLATA");
        loadLoggedUserCollectionsFromLocal();
    }

    @Override
    public void onFailureDeleteCollectionFromLocal(String errorMessage) {
        //TODO far vedere qualcosa all utente
        Log.e("errore local cancellazione", errorMessage);

    }

    @Override
    public void onSuccessDeleteCollectionFromRemote(Collection deletedCollection) {
        bookLocalDataSource.deleteCollection(deletedCollection);
    }

    @Override
    public void onFailureDeleteCollectionFromRemote(String errorMessage) {
        //todo gestisci errori
        Log.e("errore remote cancellazione", errorMessage);
    }

    //todo trasforma in onSuccess e fai onFailure
    @Override
    public void onCreateCollectionResult(Collection collection) {
        ArrayList<Collection> oneItemCollectionList = new ArrayList<>();
        oneItemCollectionList.add(collection);
        bookLocalDataSource.insertCollectionList(oneItemCollectionList);
    }

    @Override
    public void onSuccessAddBookToCollectionFromRemote(String collectionId, OLWorkApiResponse book) {
        bookLocalDataSource.addBookToCollection(collectionId, book);
    }

    @Override
    public void onSuccessUpdateCollectionFromLocal() {
        Log.d("CollectionRepository", "Collection updated successfully");
        bookLocalDataSource.getAllCollections();
    }

    @Override
    public void onFailureUpdateCollectionFromLocal(String message) {
        Log.e("CollectionRepository", message);
    }

    @Override
    public void onSuccessRemoveBookFromCollectionFromRemote(String collectionId, String bookId) {
        Log.d("cancellazione remota ok", "cancellazione remota ok");
        bookLocalDataSource.removeBookFromCollection(collectionId, bookId);
    }

    @Override
    public void onFailureRemoveBookFromCollectionFromRemote(String message) {
        Log.e("CollectionRepository", message);

    }

    @Override
    public void onFailureAddBookToCollectionFromRemote(String message) {
        Log.e("CollectionRepository", message);
    }

    @Override
    public void onSuccessDeleteAllCollectionsFromLocal() {
        allCollectionsDeletedResult.postValue(true);
        Log.d("CollectionRepository", "Collections deletion succeeded");
    }

    @Override
    public void onFailureDeleteAllCollectionsFromLocal(String message) {
        allCollectionsDeletedResult.postValue(false);
        Log.e("CollectionRepository", message);
    }

    @Override
    public void onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        collectionRemoteDataSource.fetchWorksForCollections(collections, LOGGED_USER);
    }

    @Override
    public void onFailureFetchLoggedUserCollectionsFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessFetchOtherUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        collectionRemoteDataSource.fetchWorksForCollections(collections, OTHER_USER);
    }

    @Override
    public void onFailureFetchOtherUserCollectionsFromRemoteDatabase(String message) {

    }
}
