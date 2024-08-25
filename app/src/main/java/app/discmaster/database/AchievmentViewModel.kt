package app.discmaster.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.discmaster.database.entities.Achievment

import kotlinx.coroutines.launch
import java.util.UUID

class AchievmentViewModel(private val achievmentRepository: AchievmentRepository) : ViewModel() {


    private val _achievments = MutableLiveData<List<Achievment>>()
    val achievments: LiveData<List<Achievment>> = _achievments
    fun insert(achievment: Achievment) {
        viewModelScope.launch {
            achievmentRepository.insert(achievment)
        }
    }

    fun delete(uuid: UUID) {
        viewModelScope.launch {
            achievmentRepository.delete(uuid)
        }
    }

    fun getAchievments()  {
        viewModelScope.launch {
            _achievments.value = emptyList()
            val achievments = achievmentRepository.getAllAchievments()
            _achievments.postValue(achievments)
        }


    }
    class AchievmentViewModelFactory(private val achievmentRepository: AchievmentRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AchievmentViewModel(achievmentRepository) as T
        }
    }
}