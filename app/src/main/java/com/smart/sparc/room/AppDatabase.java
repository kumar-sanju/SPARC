package com.smart.sparc.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserCredential.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserCredentialDao userCredentialDao();
}

