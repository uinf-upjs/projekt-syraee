package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import app.discmaster.database.entities.Event

@Dao
interface EventDao {

    @Upsert
    suspend fun insert(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM event")
    suspend fun getAllEvents(): List<Event>
}