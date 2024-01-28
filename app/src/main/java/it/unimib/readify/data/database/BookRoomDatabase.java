package it.unimib.readify.data.database;

import android.content.Context;

import androidx.room.RoomDatabase;

public abstract class BookRoomDatabase extends RoomDatabase {
    //todo implementa metodi, questa è una bozza per non avere errori
    private static volatile BookRoomDatabase INSTANCE;
    public abstract BookDao bookDao();


    public static BookRoomDatabase getDatabase(final Context context) {

        return INSTANCE;
    }
}