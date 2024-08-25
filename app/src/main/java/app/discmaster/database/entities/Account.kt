package app.discmaster.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity()
data class Account (
    val name: String,
    val surname: String,
    val login: String,
    val password: String,
    val email: String,
    val club: String,
    val mainHand: String,
    val achievmentId: UUID? = null

) : Serializable {

    @PrimaryKey
    var uuidAcc: UUID = UUID.randomUUID()

}