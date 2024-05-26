package it.unimib.readify.data.source.collection;

import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import it.unimib.readify.data.database.BookDao;
import it.unimib.readify.data.database.BookRoomDatabase;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class CollectionLocalDataSource extends BaseCollectionLocalDataSource {
    private final BookDao bookDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public CollectionLocalDataSource(BookRoomDatabase bookRoomDatabase,
                                     SharedPreferencesUtil sharedPreferencesUtil,
                                     DataEncryptionUtil dataEncryptionUtil
    ) {
        this.bookDao = bookRoomDatabase.bookDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void initLocalCollections(List<Collection> collectionsToInsert) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            int collectionsDeleted = bookDao.deleteAllCollections();
            if(collectionsDeleted >= 0){
                insertCollectionList(collectionsToInsert);
            }
        });
    }

    @Override
    public void getAllCollections() {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Collection> collectionList;
            collectionList = bookDao.getAllCollections();
            collectionResponseCallback.onSuccessFetchCollectionsFromLocal(collectionList);
        });
    }

    @Override
    public void insertCollectionList(List<Collection> collectionsToInsert) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {

            List<Collection> localCollections = bookDao.getAllCollections();

            if(collectionsToInsert != null){
                for(Collection collection : collectionsToInsert){
                    if(! localCollections.contains(collection)){
                        localCollections.add(collection);
                        Long id = insertCollection(collection);
                        Log.e(" LONG ID", String.valueOf(id));
                    }
                }
            }

            //todo gestisci errore se id = -1
            //bookDao.insertCollectionList(localCollections);
            //Log.e("OUTPUT LONG LIST", collectionsInsertedIds.toString());

            // sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

            collectionResponseCallback.onSuccessInsertCollectionFromLocal(localCollections);

        });
    }

    private Long insertCollection(Collection collectionToInsert) {
        AtomicLong id = new AtomicLong(-1);
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToInsert != null){
                List<Collection> localCollections = bookDao.getAllCollections();
                if(! localCollections.contains(collectionToInsert)) {
                    id.set(bookDao.insertCollection(collectionToInsert));
                }
            }
        });
        return id.get();
    }

    @Override
    public void updateCollection(Collection collectionToUpdate) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToUpdate != null){
                int result = bookDao.updateCollection(collectionToUpdate);
                Log.d("Update result", String.valueOf(result));
                collectionResponseCallback.onSuccessUpdateCollectionFromLocal();
            } else {
                collectionResponseCallback.onFailureUpdateCollectionFromLocal("UPDATE COLLECTION ERROR");
            }
        });

    }

    @Override
    public void deleteCollection(Collection collectionToDelete) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToDelete != null){
                bookDao.deleteCollection(collectionToDelete);
                collectionResponseCallback.onSuccessDeleteCollectionFromLocal(collectionToDelete);
            } else {
                collectionResponseCallback.onFailureDeleteCollectionFromLocal("DELETE COLLECTION ERROR");
            }
        });
    }

    @Override
    public void deleteAllCollections() {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            int collectionCounter = bookDao.getAllCollections().size();
            int collectionsDeleted = bookDao.deleteAllCollections();

            // It means that everything has been deleted
            if (collectionCounter == collectionsDeleted) {
                sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);

                collectionResponseCallback.onSuccessDeleteAllCollectionsFromLocal();
            } else {
                collectionResponseCallback.onFailureDeleteAllCollectionsFromLocal("COLLECTIONS DELETION FAILED");

            }
        });
    }

    @Override
    public void addBookToCollection(String collectionId, OLWorkApiResponse book) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = bookDao.getCollectionById(collectionId);
            int numberOfBooks = collectionToUpdate.getNumberOfBooks();
            Log.d("numberOfBooks", String.valueOf(numberOfBooks));
            numberOfBooks++;

            List<String> books = collectionToUpdate.getBooks();
            books.add(book.getKey());
            List<OLWorkApiResponse> works = collectionToUpdate.getWorks();
            works.add(book);
            collectionToUpdate.setNumberOfBooks(numberOfBooks);
            collectionToUpdate.setBooks(books);
            collectionToUpdate.setWorks(works);

            updateCollection(collectionToUpdate);
        });
    }

    @Override
    public void removeBookFromCollection(String collectionId, String bookId) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            Collection collectionToUpdate = bookDao.getCollectionById(collectionId);
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

            updateCollection(collectionToUpdate);
        });
    }
}
