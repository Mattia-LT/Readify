package it.unimib.readify.data.source.collection;

import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.OPERATION_ADD_TO_COLLECTION;
import static it.unimib.readify.util.Constants.OPERATION_CHANGE_COLLECTION_VISIBILITY;
import static it.unimib.readify.util.Constants.OPERATION_REMOVE_FROM_COLLECTION;
import static it.unimib.readify.util.Constants.OPERATION_RENAME_COLLECTION;
import static it.unimib.readify.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import it.unimib.readify.data.database.CollectionDao;
import it.unimib.readify.data.database.CollectionRoomDatabase;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class CollectionLocalDataSource extends BaseCollectionLocalDataSource {

    private final String TAG = CollectionLocalDataSource.class.getSimpleName();

    private final CollectionDao collectionDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public CollectionLocalDataSource(CollectionRoomDatabase collectionRoomDatabase,
                                     SharedPreferencesUtil sharedPreferencesUtil,
                                     DataEncryptionUtil dataEncryptionUtil
    ) {
        this.collectionDao = collectionRoomDatabase.bookDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void initLocalCollections(List<Collection> collectionsToInsert) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            int collectionsDeleted = collectionDao.deleteAllCollections();
            if(collectionsDeleted >= 0){
                insertCollectionList(collectionsToInsert);
            }
        });
    }

    @Override
    public void getAllCollections() {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Collection> collectionList;
            collectionList = collectionDao.getAllCollections();
            if(collectionList != null){
                collectionResponseCallback.onSuccessFetchCollectionsFromLocal(collectionList);
            } else {
                collectionResponseCallback.onFailureFetchCollectionsFromLocal(TAG + "Error: an error occurred while fetching collections from local database");
            }
        });
    }

    @Override
    public void insertCollectionList(List<Collection> collectionsToInsert) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {

            List<Collection> localCollections = collectionDao.getAllCollections();

            if(collectionsToInsert != null){
                for(Collection collection : collectionsToInsert){
                    if(!localCollections.contains(collection)){
                        localCollections.add(collection);
                        Long id = insertCollection(collection);
                        if(id == -1){
                            collectionResponseCallback.onFailureInsertCollectionFromLocal(TAG + " - insertCollectionList, Error with collection : " + collection.getName() + ", operation cancelled.");
                        }
                    }
                }
            }
            collectionResponseCallback.onSuccessInsertCollectionFromLocal(localCollections);
        });
    }

    private Long insertCollection(Collection collectionToInsert) {
        AtomicLong id = new AtomicLong(-1);
        CountDownLatch latch = new CountDownLatch(1);

        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                if (collectionToInsert != null) {
                    List<Collection> localCollections = collectionDao.getAllCollections();
                    if (!localCollections.contains(collectionToInsert)) {
                        id.set(collectionDao.insertCollection(collectionToInsert));
                    }
                }
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return id.get();
    }

    @Override
    public void updateCollection(Collection collectionToUpdate, String operation) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToUpdate != null){
                int result = collectionDao.updateCollection(collectionToUpdate);
                if(result >= 0){
                    switch (operation){
                        case OPERATION_ADD_TO_COLLECTION:
                            collectionResponseCallback.onSuccessAddBookToCollectionFromLocal();
                            break;
                        case OPERATION_REMOVE_FROM_COLLECTION:
                            collectionResponseCallback.onSuccessRemoveBookFromCollectionFromLocal();
                            break;
                        case OPERATION_CHANGE_COLLECTION_VISIBILITY:
                            collectionResponseCallback.onSuccessChangeCollectionVisibilityFromLocal();
                            break;
                        case OPERATION_RENAME_COLLECTION:
                            collectionResponseCallback.onSuccessRenameCollectionFromLocal();
                            break;
                    }
                    return;
                }
            }
            switch (operation){
                case OPERATION_ADD_TO_COLLECTION:
                    collectionResponseCallback.onFailureAddBookToCollectionFromLocal(TAG + " - error in update collection on local database. AddBookToCollection failed.");
                    break;
                case OPERATION_REMOVE_FROM_COLLECTION:
                    collectionResponseCallback.onFailureRemoveBookFromCollectionFromLocal(TAG + " - error in update collection on local database. RemoveBookFromCollection failed.");
                    break;
                case OPERATION_CHANGE_COLLECTION_VISIBILITY:
                    collectionResponseCallback.onFailureChangeCollectionVisibilityFromLocal(TAG + " - error in update collection on local database. ChangeCollectionVisibility failed.");
                    break;
                case OPERATION_RENAME_COLLECTION:
                    collectionResponseCallback.onFailureRenameCollectionFromLocal(TAG + " - error in update collection on local database. RenameCollection failed.");
                    break;
            }
        });
    }

    @Override
    public void deleteCollection(Collection collectionToDelete) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToDelete != null){
                collectionDao.deleteCollection(collectionToDelete);
                collectionResponseCallback.onSuccessDeleteCollectionFromLocal(collectionToDelete);
            } else {
                collectionResponseCallback.onFailureDeleteCollectionFromLocal(TAG + " - error in delete single collection: collectionToDelete was null");
            }
        });
    }

    @Override
    public void deleteAllCollections() {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            int collectionCounter = collectionDao.getAllCollections().size();
            int collectionsDeleted = collectionDao.deleteAllCollections();

            // It means that everything has been deleted
            if (collectionCounter == collectionsDeleted) {
                sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                collectionResponseCallback.onSuccessDeleteAllCollectionsFromLocal();
            } else {
                collectionResponseCallback.onFailureDeleteAllCollectionsFromLocal(TAG + " - error in deleteAllCollection: some collections couldn't be deleted.");
            }
        });
    }



    @Override
    public void addBookToCollection(String collectionId, OLWorkApiResponse book) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = collectionDao.getCollectionById(collectionId);
            int numberOfBooks = collectionToUpdate.getNumberOfBooks();
            numberOfBooks++;
            List<String> books = collectionToUpdate.getBooks();
            books.add(book.getKey());
            List<OLWorkApiResponse> works = collectionToUpdate.getWorks();
            works.add(book);

            collectionToUpdate.setNumberOfBooks(numberOfBooks);
            collectionToUpdate.setBooks(books);
            collectionToUpdate.setWorks(works);

            updateCollection(collectionToUpdate, OPERATION_ADD_TO_COLLECTION);
        });
    }

    @Override
    public void removeBookFromCollection(String collectionId, String bookId) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = collectionDao.getCollectionById(collectionId);
            int numberOfBooks = collectionToUpdate.getNumberOfBooks() - 1;
            List<String> books = collectionToUpdate.getBooks();
            books.remove(bookId);
            List<OLWorkApiResponse> works = collectionToUpdate.getWorks();

            Iterator<OLWorkApiResponse> iterator = works.iterator();
            while (iterator.hasNext()) {
                OLWorkApiResponse work = iterator.next();
                if (work.getKey().equalsIgnoreCase(bookId)) {
                    iterator.remove();
                    break;
                }
            }

            collectionToUpdate.setNumberOfBooks(numberOfBooks);
            collectionToUpdate.setBooks(books);
            collectionToUpdate.setWorks(works);

            updateCollection(collectionToUpdate, OPERATION_REMOVE_FROM_COLLECTION);
        });
    }

    @Override
    public void renameCollection(String collectionId, String newCollectionName) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = collectionDao.getCollectionById(collectionId);
            collectionToUpdate.setName(newCollectionName);
            updateCollection(collectionToUpdate, OPERATION_RENAME_COLLECTION);
        });
    }

    @Override
    public void changeCollectionVisibility(String collectionId, boolean isCollectionVisible) {
        CollectionRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = collectionDao.getCollectionById(collectionId);
            collectionToUpdate.setVisible(isCollectionVisible);
            updateCollection(collectionToUpdate, OPERATION_CHANGE_COLLECTION_VISIBILITY);
        });
    }
}