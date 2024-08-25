package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import app.discmaster.database.entities.Activity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ActivityDao {

    @Upsert
    suspend fun insert(activity: Activity)



    @Query("SELECT * FROM activity WHERE uuidAct = :uuid")
    fun getActivityById(uuid: UUID): Flow<Activity>
}