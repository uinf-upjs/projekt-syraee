package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import app.discmaster.database.entities.Event
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface EventDao {

    @Upsert
    suspend fun insert(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM event where uuidEve= :eventId")
    fun getEventById(eventId: UUID): Flow<Event>

    @Query("SELECT * FROM event")
    suspend fun getAllEvents(): List<Event>
}