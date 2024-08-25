package app.discmaster.database

import androidx.lifecycle.LiveData
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Activity
import app.discmaster.database.entities.Event
import app.discmaster.database.interfaces.AccountDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AccountRepository(private val accountDao: AccountDao) {

    suspend fun insert(account: Account){
        accountDao.insertAccount(account)
    }
    suspend fun update(account: Account) {
        accountDao.update(account)
    }

    fun getAccount(accoundId: UUID): Flow<Account> {
        return accountDao.getAccountById(accoundId)
    }

    suspend fun delete(uuid: UUID) {
        accountDao.delete(uuid)
    }
    suspend fun deleteEvent(uuid: UUID) {
        accountDao.deleteEvent(uuid)
    }


    suspend fun existingLogin(login: String): Boolean {
        val exists = accountDao.existingLogin(login)
        return exists!=0
    }
    suspend fun getEventsByAccount(accountId: UUID): List<Event>{
        val acountEvents = accountDao.getAccountWithEvents(accountId)
        return acountEvents.flatMap { it.events }
    }

    suspend fun getActivitiesByAccount(accountId: UUID): List<Activity> {
        val accountWithActivities = accountDao.getAccountWithActivities(accountId)
        return accountWithActivities.flatMap { it.activities }
    }

    suspend fun getPasswordByLogin(loginName : String): String? {
        return accountDao.getPasswordByLogin(loginName)
    }

    suspend fun getIdByLogin(loginName: String): UUID {
        return accountDao.getIdByLogin(loginName)
    }

    suspend fun checkLoginEmail(login:String, email:String): Boolean{
        val result = accountDao.checkLoginEmail(login, email)
        return result >= 1
    }

    suspend fun updatePassword(uuid: UUID, newPassword: String) {
        accountDao.updatePassword(uuid, newPassword)
    }





}