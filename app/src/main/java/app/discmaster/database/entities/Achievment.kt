package app.discmaster.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity()
data class Achievment (
    val name: String,
    val surname: String,
    val login: String,
    val password: String,
    val email: String,
    val club: String,
    val mainHand: String,
    val photo: ByteArray

) : Serializable {
    @PrimaryKey
    var uuidAch: UUID = UUID.randomUUID()

}