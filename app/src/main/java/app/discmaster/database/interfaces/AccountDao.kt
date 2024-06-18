package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.connecting.AccountWithActivities
import app.discmaster.database.entities.connecting.AccountWithEvents
import java.util.UUID

@Dao
interface AccountDao {

    @Upsert
    suspend fun insertAccount(account: Account)

    @Query("SELECT * FROM account WHERE uuidAcc = :uuid")
    suspend fun getAccountById(uuid: UUID): Account

    @Transaction
    @Query("SELECT * FROM account WHERE uuidAcc = :uuid")
    suspend fun getAccountWithActivities(uuid: UUID): List<AccountWithActivities>

    @Transaction
    @Query("SELECT * FROM account WHERE uuidAcc = :uuid")
    suspend fun getAccountWithEvents(uuid: UUID): List<AccountWithEvents>

    @Transaction
    @Query("SELECT password FROM account WHERE login =:loginName")
    suspend fun getPasswordByLogin(loginName: String): String

    @Transaction
    @Query("SELECT uuidAcc FROM account WHERE login =:loginName")
    suspend fun getIdByLogin(loginName: String): UUID


}