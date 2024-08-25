package app.discmaster.database

import app.discmaster.database.entities.Achievment

import app.discmaster.database.interfaces.AchievmentDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID


class AchievmentRepository(private val achievmentDao: AchievmentDao) {

    suspend fun insert(achievment: Achievment){
        achievmentDao.insert(achievment)
    }

    suspend fun delete(uuid: UUID){
        achievmentDao.delete(uuid)
    }

    suspend fun getAllAchievments(): List<Achievment>{
        return achievmentDao.getAllAchievments()
    }
}