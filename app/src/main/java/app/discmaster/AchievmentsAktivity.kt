package app.discmaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.AchievmentViewModel
import app.discmaster.database.entities.Achievment
import app.discmaster.database.entities.Event
import app.discmaster.ui.theme.PurpleGrey80
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AchievmentsAktivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun AchievmentScreen(achievmentViewModel: AchievmentViewModel, accountViewModel: AccountViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val loggedInUser = sharedPref.getString("logged_in_user", null)
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    LaunchedEffect(loggedInUser) {

        accountViewModel.getIdByLogin(loggedInUser!!)
    }



    val account = accountViewModel.account.observeAsState(null)

    val achivsState by achievmentViewModel.achievments.observeAsState(emptyList())

    LaunchedEffect(account.value) {
        if (account.value!= null) {
            scope.launch(Dispatchers.IO) {

                achievmentViewModel.getAchievments()
            }
        }
    }

    if (achivsState.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(
                items = achivsState,
                itemContent = {
                    AchievmentItems(achievment = it)
                })
        }
    }
}


@Composable
fun AchievmentItems(achievment: Achievment){
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .fillMaxWidth() ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = PurpleGrey80)
    ) {
        Row()
         {
            if (achievment.photo.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(achievment.photo),
                    contentDescription = achievment.name,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxHeight()
                )

            } else {
                Text(text = "No photo available")
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = achievment.name, style = MaterialTheme.typography.bodyMedium, fontSize = 17.sp, fontWeight = FontWeight.Bold, color=MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = stringResource(id = R.string.count_throws) +" : "+ achievment.count.toString(), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(11.dp))
                if(achievment.hand!="-"){
                    Text(text = stringResource(id = R.string.hand)  +" : "+ achievment.hand, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(11.dp))
                }
                if(achievment.distance!=0){
                    Text(text = stringResource(id = R.string.distance)  +" : "+ achievment.distance.toString(), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(11.dp))
                }
                if(achievment.weather!="-"){
                    Text(text = stringResource(id = R.string.weather)  +" : "+ achievment.weather, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(11.dp))
                }
                if(achievment.throwType!="-"){
                    Text(text = stringResource(id = R.string.throw_type)  +" : "+ achievment.throwType, style = MaterialTheme.typography.bodySmall)
                }








            }
        }
    }

}



