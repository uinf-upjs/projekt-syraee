package app.discmaster.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.discmaster.database.entities.Activity
import kotlinx.coroutines.launch
import java.util.UUID

class ActivityViewModel(private val activityRepository: ActivityRepository) : ViewModel(){

    fun insert(activity: Activity) {
        viewModelScope.launch {
            activityRepository.insert(activity)
        }
    }


    fun getActivitiesById(id : UUID){

    }

    class ActivityViewModelFactory(private val activityRepository: ActivityRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivityViewModel(activityRepository) as T
        }
    }
}