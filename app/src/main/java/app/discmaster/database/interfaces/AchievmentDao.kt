package app.discmaster.database.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import app.discmaster.database.entities.Achievment
import app.discmaster.database.entities.connecting.AccountWithEvents
import app.discmaster.database.entities.connecting.AchievmentWithAccounts
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AchievmentDao {

    @Insert
    suspend fun insert(achievment: Achievment)

    @Query("DELETE FROM achievment WHERE uuidAch = :uuid")
    suspend fun delete(uuid: UUID)

    @Query("SELECT * FROM achievment")
    suspend fun getAllAchievments(): List<Achievment>

    @Query("SELECT * FROM achievment WHERE uuidAch = :uuid")
    suspend fun getAchievmentById(uuid: UUID): Achievment

    @Transaction
    @Query("SELECT * FROM achievment WHERE uuidAch = :uuid")
    fun getAchievmentWithAccounts(uuid: UUID): Flow<List<AchievmentWithAccounts>>



}