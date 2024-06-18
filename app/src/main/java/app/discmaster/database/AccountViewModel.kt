package app.discmaster.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.discmaster.database.entities.Account
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class AccountViewModel (private val accountRepository: AccountRepository) : ViewModel() {

    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    fun getAccount(accountId: UUID) {
        viewModelScope.launch {
            val fetchedAccount = accountRepository.getAccount(accountId)
            _account.postValue(fetchedAccount)
        }
    }

    fun insert(account: Account) {
        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }

    fun getIdByLogin(loginName: String){
        viewModelScope.launch {
            val accountId = accountRepository.getIdByLogin(loginName)
            _account.postValue(accountRepository.getAccount(accountId))

        }
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


