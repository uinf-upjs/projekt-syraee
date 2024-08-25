package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.connecting.AccountWithActivities
import app.discmaster.database.entities.connecting.AccountWithEvents
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AccountDao {

    @Upsert
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun update(account: Account)

    @Query("SELECT * FROM account WHERE uuidAcc = :uuid")
    fun getAccountById(uuid: UUID): Flow<Account>

    @Query("DELETE FROM activity WHERE uuidAct = :uuid")
    suspend fun delete(uuid: UUID)

    @Query("DELETE FROM event WHERE uuidEve = :uuid")
    suspend fun deleteEvent(uuid: UUID)

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

    @Query("SELECT COUNT(*) FROM account WHERE login = :loginName")
    suspend fun existingLogin(loginName: String): Int

    @Query("SELECT COUNT(*) FROM account WHERE login =:login AND email =:email")
    suspend fun checkLoginEmail(login:String, email:String) : Int

    @Transaction
    @Query("UPDATE account SET password = :newPassword WHERE uuidAcc = :uuid")
    suspend fun updatePassword(uuid: UUID, newPassword: String)
}