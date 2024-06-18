package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import app.discmaster.database.entities.Activity

@Dao
interface ActivityDao {

    @Upsert
    suspend fun insert(activity: Activity)

    @Delete
    suspend fun delete(activity: Activity)
}