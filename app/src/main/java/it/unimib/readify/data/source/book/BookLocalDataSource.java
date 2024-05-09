package it.unimib.readify.data.source.book;

import android.util.Log;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import it.unimib.readify.data.database.BookDao;
import it.unimib.readify.data.database.BookRoomDatabase;
import it.unimib.readify.model.Collection;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class BookLocalDataSource extends BaseBookLocalDataSource{

    private final BookDao bookDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public BookLocalDataSource(BookRoomDatabase bookRoomDatabase,
                               SharedPreferencesUtil sharedPreferencesUtil,
                               DataEncryptionUtil dataEncryptionUtil
    ) {
        this.bookDao = bookRoomDatabase.bookDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getAllCollections() {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Collection> collectionList;
            collectionList = bookDao.getAllCollections();
            bookResponseCallback.onSuccessFetchCollectionsFromLocal(collectionList);
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

            bookResponseCallback.onSuccessInsertCollectionFromLocal(localCollections);

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
                Log.e("Update result", String.valueOf(result));
            }
        });

        //todo callback

    }

    @Override
    public void deleteCollection(Collection collectionToDelete) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(collectionToDelete != null){
                bookDao.deleteCollection(collectionToDelete);
            }

            //todo callback

        });
    }

    @Override
    public void deleteAllCollections() {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            int collectionCounter = bookDao.getAllCollections().size();
            int collectionsDeleted = bookDao.deleteAllCollections();

            // It means that everything has been deleted
            if (collectionCounter == collectionsDeleted) {
                //sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                //dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);

                //TODO sai gia cosa
            }
        });
    }
}
