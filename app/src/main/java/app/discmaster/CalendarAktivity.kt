package app.discmaster

import android.annotation.SuppressLint

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.ActivityViewModel
import app.discmaster.database.entities.Activity
import app.discmaster.database.entities.Event
import app.discmaster.ui.theme.GreenPastel
import app.discmaster.ui.theme.Pink80
import app.discmaster.ui.theme.Purple40
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols

class CalendarAktivty : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

//zdroj
//https://medium.com/@kiwi47/create-a-flexible-and-customizable-calendar-view-in-android-with-jetpack-compose-56dfb911c2ab

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarScreen(accountViewModel: AccountViewModel,activityViewModel: ActivityViewModel) {

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)
    val scope = rememberCoroutineScope()
    val currentPage = remember { mutableStateOf(0) }
    val currentMonth = remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString("logged_in_user", "Account")

    if (username != null) {
        scope.launch{ accountViewModel.getIdByLogin(username) }

    }

    val account = accountViewModel.account.observeAsState(null)

    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    val activitiesState by accountViewModel.activities.observeAsState(emptyList())
    val eventsState by accountViewModel.events.observeAsState(emptyList())

    LaunchedEffect(account.value) {
        if (account.value!= null) {
            scope.launch(Dispatchers.IO) {
                accountViewModel.getActivitiesByAccount(account.value!!.uuidAcc)
                accountViewModel.getEventsByAccount(account.value!!.uuidAcc)
            }
        }
    }
    val selectedDate = remember { mutableStateOf<Date?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val activityDates = activitiesState.map { it.date }
    val eventDates = eventsState.map { it.date}


    HorizontalPager(
        count = Int.MAX_VALUE,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        currentPage.value = page
        val month = currentMonth.value.clone() as Calendar
        month.add(Calendar.MONTH, page - currentPage.value)
        val filteredEvents = filterEventsByMonth(eventsState, month)
        val datesOfEvents = getDummyDates(month, eventDates)
        val datesOfActivities = getDummyDates(month, activityDates)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


            CalendarView(
                month = month.time,
                dateEvents = datesOfEvents,
                dateActivities = datesOfActivities,
                displayNext = true,
                displayPrev = true,
                onClickNext = {
                    currentMonth.value.add(Calendar.MONTH, 1)
                    scope.launch {
                        pagerState.scrollToPage(page + 1)
                    }
                },
                onClickPrev = {
                    currentMonth.value.add(Calendar.MONTH, -1)
                    scope.launch {
                        pagerState.scrollToPage(page - 1)
                    }
                },
                onClick = {
                    selectedDate.value = it
                    showDialog.value = true
                },
                startFromSunday = false,
                modifier = Modifier.fillMaxWidth()
            )


            Box(modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)){
                Button(onClick = {
                    val intent = Intent(context, AddEventAktivity::class.java)
                    context.startActivity(intent) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPastel
                ),) {
                    Text(text = "+ " + stringResource(id = R.string.event))
                }
            }


            Spacer(modifier = Modifier.height(8.dp))



            if (filteredEvents.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventItems(event = event)
                    }
                }
            }
        }


        if (showDialog.value && selectedDate.value != null) {
            ActivityDialog(
                date = selectedDate.value!!,
                uuid = account.value!!.uuidAcc,
                accountViewModel = accountViewModel,
                activities = activitiesState.filter {
                    val calendar = Calendar.getInstance().apply { time = it.date }
                    selectedDate.value?.let { selected ->
                        val selectedCalendar = Calendar.getInstance().apply { time = selected }
                        calendar.isSameDay(selectedCalendar)
                    } ?: false
                },
                onDismiss = { showDialog.value = false }
            )
        }


    }
}


private fun filterEventsByMonth(events: List<Event>, month: Calendar): List<Event> {
    val startOfMonth = month.clone() as Calendar
    startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val endOfMonth = month.clone() as Calendar
    endOfMonth.set(Calendar.DAY_OF_MONTH, month.getActualMaximum(Calendar.DAY_OF_MONTH))

    return events.filter { event ->
        val eventDate = Calendar.getInstance().apply { time = event.date }
        eventDate.after(startOfMonth) && eventDate.before(endOfMonth)
    }
}
private fun getDummyDates(month: Calendar, eventDates: List<Date>): ImmutableList<Pair<Date, Boolean>> {
    val dates = mutableListOf<Pair<Date, Boolean>>()
    val startOfMonth = month.clone() as Calendar
    startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val endOfMonth = month.clone() as Calendar
    endOfMonth.set(Calendar.DAY_OF_MONTH, month.getActualMaximum(Calendar.DAY_OF_MONTH))

    val activityCalendars = eventDates.map { date ->
        Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    var currentDate = startOfMonth.clone() as Calendar
    while (!currentDate.after(endOfMonth)) {
        val isHighlighted = activityCalendars.any { it.isSameDay(currentDate) }
        dates.add(Pair(currentDate.time, isHighlighted))
        currentDate.add(Calendar.DAY_OF_MONTH, 1)
    }
    return ImmutableList.copyOf(dates)
}

private fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
            this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}

private fun Date.formatToCalendarDay(): String = SimpleDateFormat("d", Locale.getDefault()).format(this)

@Composable
fun CalendarCell(
    date: Date,
    activitySignal:Boolean,
    onClick: (Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = date.formatToCalendarDay()
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                shape = RoundedCornerShape(CornerSize(8.dp)),
                color = if (activitySignal) GreenPastel else colorScheme.secondaryContainer,
            )
            .clip(RoundedCornerShape(CornerSize(8.dp)))
            .clickable { onClick(date) }
    ) {
        Text(
            text = text,
            color = colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun Int.getDayOfWeek3Letters(): String = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_WEEK, this@getDayOfWeek3Letters)
}.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

@Composable
private fun WeekdayCell(weekday: Int, modifier: Modifier = Modifier) {
    val text = weekday.getDayOfWeek3Letters()
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        Text(
            text = text,
            color = colorScheme.onPrimaryContainer,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CalendarGrid(
    dateEvents: ImmutableList<Pair<Date, Boolean>>,
    dateActivity : ImmutableList<Pair<Date, Boolean>>,
    onClick: (Date) -> Unit,
    startFromSunday: Boolean,
    modifier: Modifier = Modifier,
) {
    val weekdayFirstDay = dateEvents.first().first.formatToWeekDay()
    val weekdays = getWeekDays(startFromSunday)

    CalendarCustomLayout(modifier = modifier) {
        weekdays.forEach {
            WeekdayCell(weekday = it)
        }
        // Adds Spacers to align the first day of the month to the correct weekday
        repeat(if (startFromSunday) weekdayFirstDay - 1 else (weekdayFirstDay + 5) % 7) {
            Spacer(modifier = Modifier)
        }
        dateActivity.forEach{ datePair ->
            CalendarCell(
                date = datePair.first,
                activitySignal = datePair.second,
                onClick = { onClick(datePair.first) }
            )
        }
        }
    }


@Composable
fun CalendarCustomLayout(
    modifier: Modifier = Modifier,
    horizontalGapDp: Dp = 1.dp,
    verticalGapDp: Dp = 1.dp,
    content: @Composable () -> Unit,
) {
    val horizontalGap = with(LocalDensity.current) {
        horizontalGapDp.roundToPx()
    }
    val verticalGap = with(LocalDensity.current) {
        verticalGapDp.roundToPx()
    }
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val totalWidthWithoutGap = constraints.maxWidth - (horizontalGap * 6)
        val singleWidth = totalWidthWithoutGap / 7

        val xPos: MutableList<Int> = mutableListOf()
        val yPos: MutableList<Int> = mutableListOf()
        var currentX = 0
        var currentY = 0
        measurables.forEachIndexed { index, _ ->
            xPos.add(currentX)
            yPos.add(currentY)
            if ((index + 1) % 7 == 0) {
                currentX = 0
                currentY += singleWidth + verticalGap
            } else {
                currentX += singleWidth + horizontalGap
            }
        }


        val placeables: List<Placeable> = measurables.map { measurable ->
            measurable.measure(constraints.copy(maxHeight = singleWidth, maxWidth = singleWidth))
        }
        val height = yPos.lastOrNull()?.plus(singleWidth) ?: constraints.maxHeight
        layout(
            width = constraints.maxWidth,
            height = height,
        ) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = xPos[index],
                    y = yPos[index],
                )
            }
        }
    }
}

fun Date.formatToWeekDay(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun getWeekDays(startFromSunday: Boolean): ImmutableList<Int> {
    val lista = (1..7).toList()
    return if (startFromSunday) ImmutableList.copyOf(lista) else ImmutableList.copyOf(lista.drop(1) + lista.take(1))
}

@Composable
fun CalendarView(
    month: Date,
    dateEvents: ImmutableList<Pair<Date, Boolean>>?,
    dateActivities: ImmutableList<Pair<Date, Boolean>>?,
    displayNext: Boolean,
    displayPrev: Boolean,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    onClick: (Date) -> Unit,
    startFromSunday: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (displayPrev)
                IconButton(
                    onClick = onClickPrev,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "navigate to previous month"
                    )
                }
            if (displayNext)
                IconButton(
                    onClick = onClickNext,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = "navigate to next month"
                    )
                }
            Text(
                text = month.formatToMonthString(),
                style = typography.headlineMedium,
                color = colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (!dateEvents.isNullOrEmpty()) {
            CalendarGrid(
                dateEvents = dateEvents,
                dateActivity = dateActivities!!,
                onClick = onClick,
                startFromSunday = startFromSunday,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

val monthNamesSlovak = arrayOf("január", "február", "marec", "apríl", "máj", "jún", "júl", "august", "september", "október", "november", "december")

fun Date.formatToMonthString(): String {
    val locale = Locale.getDefault()
    val isSlovakLocale = locale.language == "sk" && locale.country == "SK"

    val monthNames = if (isSlovakLocale) {
        monthNamesSlovak
    } else {
        DateFormatSymbols.getInstance(locale).months
    }

    val dfs = DateFormatSymbols()
    dfs.months = monthNames
    val sdf = SimpleDateFormat("MMMM yyyy", dfs)
    return sdf.format(this)
}

fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(encodedImageData, 0, encodedImageData.size).asImageBitmap()
}

@Composable
fun EventItems(event:Event){
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .fillMaxWidth() ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(corner = CornerSize(10.dp))
    ) {
        Row( modifier = Modifier.clickable {
            context.startActivity(
                Intent(context, EventAktivity::class.java).apply {
                    putExtra("EVENT_UUID", event.uuidEve.toString())
                }
            )
        }) {
            if (event.photo.isNotEmpty()) {
                Image(
                    bitmap = imageBitmapFromBytes(event.photo),
                    contentDescription = "obrazok",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxHeight()
                )
            } else {
                Text(text = "No photo available")
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = event.name, style = typography.bodyMedium)
                Text(text = event.text, style = typography.bodySmall)
                Text(text = event.date.toString(), style = typography.bodySmall)
            }
        }
    }

}

@Composable
fun ActivityDialog(date: Date, uuid: UUID, accountViewModel: AccountViewModel,
                   activities: List<Activity>,
                   onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card( modifier = Modifier.size(400.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp),)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {


            val text = date.let { SimpleDateFormat("dd.MM.yyyy").format(it)}
                Text(
                    text = text,
                    style = typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(), textAlign = TextAlign.Center
                )
                LazyColumn {
                    println(activities)
                    items(activities) { activity ->
                        ActivityItems(activity = activity, accountViewModel, uuid)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItems(activity: Activity, accountViewModel: AccountViewModel, uuid: UUID) {
    Card(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(corner = CornerSize(10.dp))
    ) {
        Row(modifier = Modifier.padding(11.dp)) {
            Text(text = activity.weather, style = typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = activity.throwType, style = typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = activity.count.toString(), style = typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = activity.distance.toString(), style = typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(40.dp))
            IconButton(onClick = { accountViewModel.delete(activity.uuidAct, activity.accountId) }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete", modifier = Modifier.align(Alignment.CenterVertically) )
            }
        }
    }
}