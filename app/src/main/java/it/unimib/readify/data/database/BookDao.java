package it.unimib.readify.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.readify.model.Collection;

@Dao
public interface BookDao {

    @Query("SELECT * FROM collection ORDER BY name ASC")
    List<Collection> getAllCollections();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCollection(Collection collection);

    @Update
    int updateCollection(Collection collection);

    @Delete
    void deleteCollection(Collection collection);

    @Query("DELETE FROM collection")
    int deleteAllCollections();

    @Query("SELECT * FROM collection WHERE collection_id = :collectionId LIMIT 1")
    Collection getCollectionById(String collectionId);
}
