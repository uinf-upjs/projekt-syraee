package app.discmaster

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import app.discmaster.database.AccountRepository
import app.discmaster.database.AchievmentRepository
import app.discmaster.database.ActivityRepository
import app.discmaster.database.DiscMasterDatabase
import app.discmaster.database.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DiscMasterAplication : Application(){

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy {DiscMasterDatabase.getInstance(this, applicationScope)}
    val accountRepository by lazy {AccountRepository(database.accountDao)}
    val achievmentRepository by lazy { AchievmentRepository(database.achievmentDao) }
    val activityRepository by lazy { ActivityRepository(database.activityDao) }
    val eventRepository by lazy { EventRepository(database.eventDao) }


}