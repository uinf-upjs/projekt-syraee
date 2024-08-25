package app.discmaster.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Activity
import app.discmaster.database.entities.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class AccountViewModel (private val accountRepository: AccountRepository) : ViewModel() {

    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>> = _activities

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events


    fun getAccount(accountId: UUID){
        viewModelScope.launch {
            accountRepository.getAccount(accountId).collect { fetchedAccount ->
                _account.postValue(fetchedAccount)
            }
        }
    }

    fun insert(account: Account) {
        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }

    fun delete(uuid: UUID, uuidAcc: UUID) {
        viewModelScope.launch {
            accountRepository.delete(uuid)
            _activities.value = accountRepository.getActivitiesByAccount(uuidAcc)
        }
    }

    fun deleteEvent(uuid: UUID, uuidEve: UUID) {
        viewModelScope.launch {
            accountRepository.deleteEvent(uuid)
            _events.value = accountRepository.getEventsByAccount(uuidEve)
        }
    }
    fun update(account: Account) {
        viewModelScope.launch { accountRepository.update(account)
        }

    }

    suspend fun checkLogin(login: String) : Boolean {
        return withContext(Dispatchers.IO) {
             accountRepository.existingLogin(login)

        }

    }

    suspend fun chechForChangePassword(login: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            accountRepository.checkLoginEmail(login, email)
        }
    }

    fun updatePassword(uuid: UUID, newPassword: String) {
        viewModelScope.launch {
            accountRepository.updatePassword(uuid, newPassword)
        }
    }
    fun getActivitiesByAccount(accountId: UUID) {
        viewModelScope.launch {
            val activities = accountRepository.getActivitiesByAccount(accountId)
            _activities.postValue(activities)
        }
    }

    fun getEventsByAccount(accountId: UUID) {
        viewModelScope.launch {
            val events = accountRepository.getEventsByAccount(accountId)
            _events.postValue(events)
        }
    }

    suspend fun getIdByLogin(loginName: String) :UUID? {
        viewModelScope.launch {
            val accountId = accountRepository.getIdByLogin(loginName)
            if (accountId != null) {
                accountRepository.getAccount(accountId).collect { fetchedAccount ->
                    _account.postValue(fetchedAccount)

                }

            }

        }
        return accountRepository.getIdByLogin(loginName)
    }

    fun verifyPassword(loginName: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hashedPassword = accountRepository.getPasswordByLogin(loginName)

            if(hashedPassword!=null) {
               val passwordCheck = BCrypt.checkpw(password, hashedPassword)
                callback(passwordCheck)
            } else {
                callback(false)
            }
        }
    }


    class AccountViewModelFactory(private val accountRepository: AccountRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountViewModel(accountRepository) as T
        }
    }
}



