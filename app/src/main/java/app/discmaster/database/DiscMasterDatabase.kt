package app.discmaster.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
    version = 5
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE event ADD COLUMN name TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE achievments_new (
                uuidAch TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                count INTEGER NOT NULL,
                hand TEXT NOT NULL,
                distance TEXT NOT NULL,
                weather TEXT NOT NULL,
                throwType TEXT NOT NULL,
                photo BLOB NOT NULL
            )
        """)
                database.execSQL("DROP TABLE achievment")
                database.execSQL("ALTER TABLE achievments_new RENAME TO achievment")
            }
        }

        val MIGRATION_3_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE achievments_neww (
                uuidAch TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                count INTEGER NOT NULL,
                hand TEXT NOT NULL,
                distance TEXT NOT NULL,
                weather TEXT NOT NULL,
                throwType TEXT NOT NULL,
                photo TEXT NOT NULL
            )
        """)
                database.execSQL("DROP TABLE achievment")
                database.execSQL("ALTER TABLE achievments_neww RENAME TO achievment")
            }

        }

        val MIGRATION_4_5 = object : Migration(4,5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE achievments_neeww (
                uuidAch TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                count INTEGER NOT NULL,
                hand TEXT NOT NULL,
                distance INTEGER NOT NULL,
                weather TEXT NOT NULL,
                throwType TEXT NOT NULL,
                photo TEXT NOT NULL
            )
        """)
                database.execSQL("DROP TABLE achievment")
                database.execSQL("ALTER TABLE achievments_neeww RENAME TO achievment")
            }

        }




        fun getInstance(context: Context, scope: CoroutineScope): DiscMasterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiscMasterDatabase::class.java,
                    "app_db"
                ).addCallback(object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase){
                        super.onCreate(db)
                           scope.launch {
                               val achievmentDao = getInstance(context, scope).achievmentDao
                                achievmentDao.insert(Achievment("Nadšenec do disku",1000,"", 0, "", "","android.resource://${context.packageName}/drawable/disc_enthusiast"))
                           }

                    }
                }).build()
                    INSTANCE = instance
                    instance

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