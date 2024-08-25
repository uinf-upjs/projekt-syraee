package app.discmaster.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text
import java.io.Serializable
import java.util.Date
import java.util.UUID


@Entity()
data class Event (
    val name: String,
    val date: Date,
    val place: String,
    val kategory: String,
    val text: String,
    val photo: ByteArray,
    val accountId: UUID

) : Serializable {
    @PrimaryKey
    var uuidEve: UUID = UUID.randomUUID()

}