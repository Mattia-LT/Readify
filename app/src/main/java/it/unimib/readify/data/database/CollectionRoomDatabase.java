package it.unimib.readify.data.database;

import static it.unimib.readify.util.Constants.BOOK_DATABASE_NAME;
import static it.unimib.readify.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.readify.model.Collection;
import it.unimib.readify.util.CustomTypeConverter;

@Database(entities = {Collection.class}, version = DATABASE_VERSION)
@TypeConverters({CustomTypeConverter.class})
public abstract class CollectionRoomDatabase extends RoomDatabase {
    public abstract CollectionDao bookDao();

    private static volatile CollectionRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static CollectionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CollectionRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CollectionRoomDatabase.class, BOOK_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
