package app.discmaster


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.AchievmentViewModel
import app.discmaster.database.entities.Achievment
import app.discmaster.database.entities.Activity
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun HomeScreenAktivityFun(accountViewModel : AccountViewModel, achievmentViewModel: AchievmentViewModel){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val loggedInUser = sharedPref.getString("logged_in_user", null)

    LaunchedEffect(loggedInUser) {
        accountViewModel.getIdByLogin(loggedInUser!!)
    }

    val account = accountViewModel.account.observeAsState(null)
    val activitiesState by accountViewModel.activities.observeAsState(emptyList())
    val achivsState by achievmentViewModel.achievments.observeAsState(emptyList())

    LaunchedEffect(account.value) {
        if (account.value!= null) {
            scope.launch(Dispatchers.IO) {
                accountViewModel.getActivitiesByAccount(account.value!!.uuidAcc)
                achievmentViewModel.getAchievments()
            }
        }
    }
    val achivs = countForAchiv(context,activitiesState, loggedInUser, achivsState)
    val actualThrows = countThrowsWeekly(context, activitiesState)
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {

        ProgressBar(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .height(20.dp)
                .align(Alignment.CenterHorizontally),
            width = 250.dp,
            backgroundColor = Color.LightGray,
            foregroundColor = Color(0xFF6650a4),
            numberOfThrows = 1000,
            actualThrows = actualThrows
        )
        
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (achivs.isNotEmpty()) {
                LazyColumn {
                    items(achivs) { achiv ->

                        Image(
                            painter = rememberImagePainter(achiv.photo),
                            contentDescription = achiv.name,
                            modifier = Modifier.size(450.dp)
                        )

                    }
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.no_achiv),
                    contentDescription = "default",
                    modifier = Modifier.size(400.dp)
                )
            }
        }


    }

}

fun countForAchiv(context : Context,activities: List<Activity>, user : String?, achivList : List<Achievment>) : MutableList<Achievment> {
    val possibleAchivs = mutableListOf<Achievment>()
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val globalCount = sharedPref.getInt("global_count",0)

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val currentMonthActivities = activities.filter { activity ->
        val activityCal = Calendar.getInstance().apply { time = activity.date }
        activityCal.get(Calendar.MONTH) == currentMonth && activityCal.get(Calendar.YEAR) == currentYear
    }
    for (achiv in achivList) {
        when (achiv.name) {
            "Nadšenec do disku","Závislák do disku", "Sto-tisícový hádzač", "Miliónový hádzač" -> {
                val totalThrows = currentMonthActivities.sumOf { it.count }
                if (totalThrows >= achiv.count) {
                    possibleAchivs.add(achiv)
                }}
            "Mokrý rúcač" -> {
                val string = context.getString(R.string.rainy)
                val totalThrowsInRain = currentMonthActivities
                    .filter { it.weather == string || it.weather=="Rainy" }
                    .sumOf { it.count }
                if (totalThrowsInRain >= achiv.count) {
                    possibleAchivs.add(achiv)
                }}
            "Eskimák" -> {
                val string = context.getString(R.string.snowing)
                val totalThrowsInSnow = currentMonthActivities
                    .filter { it.weather == string || it.weather=="Snowing" }
                    .sumOf { it.count }
                if (totalThrowsInSnow >= achiv.count) {
                    possibleAchivs.add(achiv)
                } }
            "Šampión vánku" -> {
                val string = context.getString(R.string.windy)
                val totalThrowsInWind = currentMonthActivities
                    .filter { it.weather == string || it.weather=="Windy" }
                    .sumOf { it.count }
                if (totalThrowsInWind >= achiv.count) {
                    possibleAchivs.add(achiv)
                }}
            "Dlhý backhand", "Dlhý forehand", "Dlhý overhead" -> {
                if (currentMonthActivities.any { it.distance >= achiv.distance && it.throwType == achiv.throwType && it.count >= achiv.count }) {
                    possibleAchivs.add(achiv)
                }}
            "Krátky backhand", "Krátky forehand", "Krátky overhead" -> {
                if (currentMonthActivities.any { it.distance <= achiv.distance && it.throwType == achiv.throwType && it.count >= achiv.count }) {
                    possibleAchivs.add(achiv)
                }}

            "Obojručne zručný", "Obojručne zručnejší", "Obojručne najzručnejší" -> {
                val string = context.getString(R.string.weak_hand)
                val handCount = currentMonthActivities.filter { it.hand == string || it.hand == "Weak" }
                    .sumOf { it.count }
                if (handCount >= achiv.count) {
                    possibleAchivs.add(achiv)
                }}

            "Vianočný špeciál" -> {
                val isChristmas = currentMonthActivities.any { activity ->
                    val cal = Calendar.getInstance().apply { time = activity.date }
                    cal.get(Calendar.MONTH) == Calendar.DECEMBER
                }
                if (isChristmas && currentMonthActivities.sumOf { it.count } >= achiv.count) {
                    possibleAchivs.add(achiv)
                }}
        }
    }
    return possibleAchivs
}


fun resetWeekly(context: Context){
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val lastReset = sharedPref.getLong("last_reset", System.currentTimeMillis())
    val currentCalendar = Calendar.getInstance()
    val lastResetCalendar = Calendar.getInstance()
    lastResetCalendar.timeInMillis = lastReset


    lastResetCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    lastResetCalendar.add(Calendar.WEEK_OF_YEAR, -1)


    if(currentCalendar.get(Calendar.WEEK_OF_YEAR) != lastResetCalendar.get(Calendar.WEEK_OF_YEAR)){
        val editor = sharedPref.edit()
        editor.putLong("last_reset", System.currentTimeMillis())
        editor.apply()

    }


}

fun countThrowsWeekly(context: Context, activities: List<Activity>): Int{
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
         set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val weekStart = calendar.time
    var count = 0

    calendar.add(Calendar.WEEK_OF_YEAR, 1)
    val weekEnd = calendar.time
    for (activity in activities){
        if(activity.date>=weekStart && activity.date<weekEnd){
            count+=activity.count
            if(count>=1000){
                count = 1000
            }
        }
    }
    return count
}
@Composable
fun ProgressBar(
    modifier: Modifier,
    width: Dp,
    backgroundColor: Color,
    foregroundColor: Color,
    numberOfThrows: Int,
    actualThrows: Int,
    ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(id = R.string.weekly), fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = modifier
                .background(backgroundColor)
                .width(width)
        ) {
            Box(
                modifier = Modifier
                    .background(foregroundColor)
                    .width(width * (actualThrows.toFloat() / numberOfThrows.toFloat()))
                    .height(25.dp)
            )


                Text(
                    text = "${actualThrows} / $numberOfThrows",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

        }
    }
}






