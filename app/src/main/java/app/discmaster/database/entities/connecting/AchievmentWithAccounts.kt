package app.discmaster.database.entities.connecting


import androidx.room.Embedded
import androidx.room.Relation
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Achievment

data class AchievmentWithAccounts (
    @Embedded val achievment: Achievment,
    @Relation(
        parentColumn = "uuidAch",
        entityColumn = "achievmentId"
    )
    val accounts: Account
)