package app.discmaster.database


import app.discmaster.database.interfaces.EventDao

class EventRepository(private val eventDao: EventDao) {
}