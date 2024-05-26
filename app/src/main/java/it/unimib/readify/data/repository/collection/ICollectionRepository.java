package it.unimib.readify.data.repository.collection;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;

public interface ICollectionRepository {
    void loadLoggedUserCollectionsFromLocal();
    void fetchLoggedUserCollections(String idToken);
    void fetchOtherUserCollections(String otherUserIdToken);
    void createCollection(String bookId, String collectionName, boolean visibility);
    void deleteCollection(String idToken, Collection collection);
    void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId);
    void removeBookFromCollection(String idToken, String bookId, String collectionId);
    void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName);
    void emptyLocalDb();
    MutableLiveData<List<Result>> getLoggedUserCollectionListLiveData();
    MutableLiveData<List<Result>> getOtherUserCollectionListLiveData();
    MutableLiveData<Boolean> getAllCollectionsDeletedResult();

}
