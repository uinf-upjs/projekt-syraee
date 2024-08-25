package app.discmaster.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.discmaster.database.entities.Event
import kotlinx.coroutines.launch
import java.util.UUID

class EventViewModel (private val eventRepository: EventRepository) : ViewModel(){


    private val _event= MutableLiveData<Event>()
    val event: LiveData<Event> = _event
    fun insert(event: Event) {
        viewModelScope.launch {
            eventRepository.insert(event)
        }
    }

    fun getEvent(eventId: UUID){
        viewModelScope.launch {
            eventRepository.getEvent(eventId).collect { fetchedAccount ->
                _event.postValue(fetchedAccount)
            }
        }
    }

    class EventViewModelFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EventViewModel(eventRepository) as T
        }
    }
}