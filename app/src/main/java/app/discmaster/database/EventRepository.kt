package app.discmaster.database


import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Activity
import app.discmaster.database.entities.Event
import app.discmaster.database.interfaces.EventDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class EventRepository(private val eventDao: EventDao) {

    suspend fun insert(event: Event){
        eventDao.insert(event)
    }

    fun getEvent(eventId: UUID): Flow<Event> {
        return eventDao.getEventById(eventId)
    }
}