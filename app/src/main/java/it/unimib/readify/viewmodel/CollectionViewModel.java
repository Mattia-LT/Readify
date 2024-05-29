package it.unimib.readify.viewmodel;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.readify.data.repository.collection.ICollectionRepository;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;

public class CollectionViewModel extends ViewModel {

    private final ICollectionRepository collectionRepository;
    private MutableLiveData<List<Result>> loggedUserCollectionListLiveData;
    private MutableLiveData<List<Result>> otherUserCollectionListLiveData;
    private MutableLiveData<Boolean> isCollectionNameValid;
    private MutableLiveData<Boolean> isCollectionNameUnique;
    private MutableLiveData<String> newCollectionName;
    private MutableLiveData<Boolean> deleteAllCollectionResult;
    private MutableLiveData<Boolean> addToCollectionResult;
    private MutableLiveData<Boolean> removeFromCollectionResult;


    public CollectionViewModel(ICollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public MutableLiveData<List<Result>> getLoggedUserCollectionListLiveData(){
        if(loggedUserCollectionListLiveData == null){
            loggedUserCollectionListLiveData = collectionRepository.getLoggedUserCollectionListLiveData();
        }
        return loggedUserCollectionListLiveData;
    }

    //todo togli ovunque tranne che in home fragment, per caricare le collections dobbiamo usare il
    //todo metodo load collections
    public void fetchLoggedUserCollections(String idToken){
        collectionRepository.fetchLoggedUserCollections(idToken);
    }

    public void loadLoggedUserCollections(){
        collectionRepository.loadLoggedUserCollectionsFromLocal();
    }

    public MutableLiveData<List<Result>> getOtherUserCollectionListLiveData(){
        if(otherUserCollectionListLiveData == null){
            otherUserCollectionListLiveData = collectionRepository.getOtherUserCollectionListLiveData();
        }
        return otherUserCollectionListLiveData;
    }

    public void fetchOtherUserCollections(String otherUserIdToken){
        collectionRepository.fetchOtherUserCollections(otherUserIdToken);
    }

    public void createCollection(String idToken, String collectionName, boolean visible){
        collectionRepository.createCollection(idToken, collectionName, visible);
    }

    public void deleteCollection(String idToken, Collection collection){
        collectionRepository.deleteCollection(idToken, collection);
    }

    public void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId) {
        collectionRepository.addBookToCollection(idToken, book, collectionId);
    }

    public void removeBookFromCollection(String idToken, String bookId, String collectionId) {
        collectionRepository.removeBookFromCollection(idToken, bookId, collectionId);
    }

    //Rename collection section
    public void setNewCollectionName(String name) {
        if(newCollectionName == null){
            newCollectionName = new MutableLiveData<>();
        }
        newCollectionName.setValue(name);
    }

    public LiveData<String> getCollectionName(){
        if(newCollectionName == null){
            newCollectionName = new MutableLiveData<>();
        }
        return newCollectionName;
    }

    public LiveData<Boolean> isNameValid() {
        if(isCollectionNameValid == null){
            isCollectionNameValid = new MutableLiveData<>();
        }
        return isCollectionNameValid;
    }

    public LiveData<Boolean> isNameUnique() {
        if(isCollectionNameUnique == null){
            isCollectionNameUnique = new MutableLiveData<>();
        }
        return isCollectionNameUnique;
    }

    public void validateCollectionName() {
        String collectionName = newCollectionName.getValue();

        if (collectionName != null && !collectionName.isEmpty() && isValidCollectionFormat(collectionName)) {
            isCollectionNameValid.setValue(true);
            if(checkUniqueCollectionName(collectionName)){
                isCollectionNameUnique.setValue(true);
            } else {
                isCollectionNameUnique.setValue(false);
            }
        } else {
            isCollectionNameValid.setValue(false);
        }
    }

    private boolean isValidCollectionFormat(String name) {
        //todo aggiungere ulteriori controlli in caso
        return name.length() <= COLLECTION_NAME_CHARACTERS_LIMIT &&
                !name.isEmpty() &&
                name.length() > 4;
    }

    private boolean checkUniqueCollectionName(String name) {
        List<Result> collections = loggedUserCollectionListLiveData.getValue();
        if (collections != null) {
            for (Result collectionResult : collections) {
                Collection collection = ((Result.CollectionSuccess) collectionResult).getData();
                if (collection.getName().equalsIgnoreCase(name)) {
                    return false; // Name already exists
                }
            }
        }return true; // Name is unique
    }


    public void renameCollection(String loggedUserIdToken, String collectionId) {
        collectionRepository.renameCollection(loggedUserIdToken, collectionId, newCollectionName.getValue());
        this.isCollectionNameValid = new MutableLiveData<>();
        this.isCollectionNameUnique = new MutableLiveData<>();
    }
    //End of Rename collection section

    public void emptyLocalDb(){
        collectionRepository.emptyLocalDb();
    }

    public MutableLiveData<Boolean> getDeleteAllCollectionResult() {
        if(deleteAllCollectionResult == null){
            deleteAllCollectionResult = collectionRepository.getAllCollectionsDeletedResult();
        }
        return deleteAllCollectionResult;
    }

    public MutableLiveData<Boolean> getAddToCollectionResult() {
        if(addToCollectionResult == null){
            addToCollectionResult = collectionRepository.getAddToCollectionResult();
        }
        return addToCollectionResult;
    }

    public MutableLiveData<Boolean> getRemoveFromCollectionResult() {
        if(removeFromCollectionResult == null){
            removeFromCollectionResult = collectionRepository.getRemoveFromCollectionResult();
        }
        return removeFromCollectionResult;
    }

    public void changeCollectionVisibility(String loggedUserIdToken, String collectionId, boolean isCollectionVisible){
        collectionRepository.changeCollectionVisibility(loggedUserIdToken, collectionId, isCollectionVisible);
    }
}
