package app.discmaster.database

import app.discmaster.database.entities.Account
import app.discmaster.database.interfaces.AccountDao
import java.util.UUID

class AccountRepository(private val accountDao: AccountDao) {

    suspend fun insert(account: Account){
        accountDao.insertAccount(account)
    }

    suspend fun getAccount(accoundId: UUID): Account{
        return accountDao.getAccountById(accoundId)
    }

    suspend fun getEventsByAccount(accountId: UUID){
        accountDao.getAccountWithEvents(accountId)
    }

    suspend fun getActivitiesByAccount(accountId: UUID){
        accountDao.getAccountWithActivities(accountId)
    }

    suspend fun getPasswordByLogin(loginName : String): String? {
        return accountDao.getPasswordByLogin(loginName)
    }

    suspend fun getIdByLogin(loginName: String): UUID {
        return accountDao.getIdByLogin(loginName)
    }





}