package app.discmaster.database.entities.connecting

import androidx.room.Embedded
import androidx.room.Relation
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Event

data class AccountWithEvents (
    @Embedded val account: Account,
    @Relation(
        parentColumn = "uuidAcc",
        entityColumn = "accountId"
    )
    val events: List<Event>
)