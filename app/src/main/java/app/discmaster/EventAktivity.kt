package app.discmaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.EventViewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EventAktivity : ComponentActivity() {

    private val eventUUID: UUID? by lazy {
        val uuidString = intent?.getStringExtra("EVENT_UUID")
        uuidString?.let { UUID.fromString(it) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Log.d("EventActivity", "Received UUID: $eventUUID")
            eventUUID?.let {
                eventViewModel.getEvent(it)
                EventScreen(eventViewModel = eventViewModel, accountViewModel = accountViewModel)
            } ?: run {
                Text(text = "Invalid event UUID")
            }}
        }


    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory((application as DiscMasterAplication).eventRepository)
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EventScreen(eventViewModel: EventViewModel, accountViewModel: AccountViewModel){
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val loggedInUser = sharedPref.getString("logged_in_user", null)
    val scope = rememberCoroutineScope()
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    LaunchedEffect(loggedInUser) {

        accountViewModel.getIdByLogin(loggedInUser!!)
    }

    val account = accountViewModel.account.observeAsState(null)



    val event = eventViewModel.event.observeAsState(null)

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    if (event.value !=null) {
                        Image(
                            modifier = Modifier
                                .heightIn(max = 1000.dp)
                                .fillMaxWidth(),
                            bitmap = imageBitmapFromBytes(event.value!!.photo),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                            contentAlignment = Alignment.CenterEnd){Row(verticalAlignment = Alignment.CenterVertically){
                            IconButton(onClick = { }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit")
                            }
                            IconButton(onClick = {
                                accountViewModel.deleteEvent(event.value!!.uuidEve, event.value!!.accountId)
                                val intent = Intent(context, MenuAktivity::class.java)
                                context.startActivity(intent)}) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete")
                            }


                        }}
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = event.value!!.name, modifier = Modifier.padding(start = 20.dp), fontSize = 50.sp)
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(text = formatDate( event.value!!.date), modifier = Modifier.padding(top = 10.dp), fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(painter = painterResource(id = R.drawable.location_on), contentDescription = "location", modifier= Modifier
                                .padding(start = 20.dp)
                                .size(30.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = event.value!!.place, fontSize = 20.sp)


                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Category, contentDescription = "category",modifier= Modifier
                                .padding(start = 20.dp)
                                .size(30.dp) )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = event.value!!.kategory, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(modifier = Modifier.padding(bottom = 4.dp, start=20.dp, end=20.dp))
                        Text(text = event.value!!.text, fontSize = 20.sp, modifier = Modifier.padding(start=20.dp, end = 20.dp))
                    }
                }
            }
        }
    }

}


private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(date)
}
