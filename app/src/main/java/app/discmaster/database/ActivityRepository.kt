package app.discmaster.database



import app.discmaster.database.entities.Activity
import app.discmaster.database.interfaces.ActivityDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ActivityRepository(private val activityDao: ActivityDao) {

    suspend fun insert(activity: Activity){
        activityDao.insert(activity)
    }



}