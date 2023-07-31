package com.smart.sparc.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserCredentialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserCredential(UserCredential userCredential);

    @Query("SELECT * FROM user_credentials LIMIT 1")
    UserCredential getUserCredential();
}
