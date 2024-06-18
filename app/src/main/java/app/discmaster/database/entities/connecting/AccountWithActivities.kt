package app.discmaster.database.entities.connecting

import androidx.room.Embedded
import androidx.room.Relation
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Activity

data class AccountWithActivities (
    @Embedded val account: Account,
    @Relation(
        parentColumn = "uuidAcc",
        entityColumn = "accountId"
    )
    val activities: List<Activity>
)