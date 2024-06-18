package app.discmaster.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Achievment
import app.discmaster.database.entities.Activity
import app.discmaster.database.entities.Event
import app.discmaster.database.interfaces.AccountDao
import app.discmaster.database.interfaces.AchievmentDao
import app.discmaster.database.interfaces.ActivityDao
import app.discmaster.database.interfaces.EventDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID


@Database(
    entities = [Account::class, Achievment::class, Activity::class, Event::class],
    version = 1
)
@TypeConverters(UuidConverter::class)
abstract class DiscMasterDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val activityDao: ActivityDao
    abstract val eventDao: EventDao
    abstract val achievmentDao: AchievmentDao

    companion object {

        @Volatile
        private var INSTANCE: DiscMasterDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): DiscMasterDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DiscMasterDatabase::class.java,
                    "app_db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let {
                            scope.launch{
                                it.accountDao.insertAccount(Account("Alzbeta", "Andrejkova", "alzbeta", "123456", "andrejkovaalzbeta@gmail.com", "KEFEAR", "prava"))
                            }
                        }
                    }
                })



                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}

class UuidConverter {

    @TypeConverter
    fun uuidToString(uuid: UUID) = uuid.toString()

    @TypeConverter
    fun stringToUuid(string: String) = UUID.fromString(string)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}