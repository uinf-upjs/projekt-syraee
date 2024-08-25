package app.discmaster.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "achievment")
data class Achievment (
    val name: String,
    val count: Int,
    val hand: String,
    val distance: Int,
    val weather: String,
    val throwType: String,
    val photo: String

) : Serializable {
    @PrimaryKey
    var uuidAch: UUID = UUID.randomUUID()

}