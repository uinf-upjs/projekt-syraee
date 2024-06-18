package app.discmaster.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Entity()
data class Activity (
    val date: Date,
    val count: Int,
    val hand: String,
    val distance: Int,
    val weather: String,
    val throwType: String,
    val accountId: UUID

) : Serializable {
    @PrimaryKey
    var uuidAct: UUID = UUID.randomUUID()

}