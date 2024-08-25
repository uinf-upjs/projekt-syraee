package app.discmaster

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.ActivityViewModel
import app.discmaster.database.entities.Activity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddRecordAktivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddRecordScreen(accountViewModel = accountViewModel, activityViewModel = activityViewModel)
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }

    private val activityViewModel: ActivityViewModel by viewModels {
        ActivityViewModel.ActivityViewModelFactory((application as DiscMasterAplication).activityRepository)
    }
}

// date picker https://www.youtube.com/watch?v=cJxo96eTHVU


@SuppressLint("SimpleDateFormat", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(accountViewModel: AccountViewModel, activityViewModel: ActivityViewModel) {

    val year: Int
    val month: Int
    val day: Int
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()


    val date=remember { mutableStateOf(Date()) }
    val datePickerDialog = DatePickerDialog(context,R.style.DatePickerDialogTheme,
    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->

        calendar.set(year, month, dayOfMonth)
        date.value = calendar.time

        }, year, month, day
    )

    var pocetHodov by  remember {  mutableStateOf("") }
    val hands = listOf(stringResource(id = R.string.dominant_hand), stringResource(id = R.string.weak_hand))
    var selectedHand by remember { mutableStateOf(hands[0]) }
    var isExpandedHand by remember {
        mutableStateOf(false)
    }


    var distance by  remember { mutableStateOf("") }
    val weather = listOf(stringResource(id = R.string.sunny), stringResource(id = R.string.windy), stringResource(id = R.string.rainy), stringResource(id = R.string.snowing))
    var selectedWeather by remember { mutableStateOf(weather[0]) }
    var isExpandedWeather by remember {
        mutableStateOf(false)
    }


    val throwType = listOf("forehand", "backhand", "overhead")
    var selectedThrow by remember { mutableStateOf(throwType[0]) }
    var isExpandedThrow by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString("logged_in_user", "Account")
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    LaunchedEffect(username) {

        accountViewModel.getIdByLogin(username!!)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val account = accountViewModel.account.observeAsState(null)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        })

    {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{Text(
            text = stringResource(id = R.string.activity_heading),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,modifier = Modifier.fillMaxWidth()
        )}

        item{ Spacer(modifier = Modifier.height(30.dp))}
        item{ Row(verticalAlignment = Alignment.CenterVertically) {

            TextField(value = date.value.let { SimpleDateFormat("dd.MM.yyyy").format(it) } ?: "",
                onValueChange = {},
                label = {
                    Text(
                        stringResource(id = R.string.date)
                    )
                },
                readOnly = true,
                modifier = Modifier
                    .padding(9.dp)
                    .width(110.dp)
            )


            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "kalendar",
                    modifier = Modifier.size(50.dp)
                )
            }

        }}

        item{Spacer(modifier = Modifier.height(25.dp))}

        item{Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = pocetHodov, onValueChange = { pocetHodov = it }, label = {
                Text(
                    stringResource(id = R.string.count_throws)
                )
            }, modifier = Modifier
                .width(160.dp)
                .padding(9.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(30.dp))
            ExposedDropdownMenuBox(
                expanded = isExpandedHand,
                onExpandedChange = { isExpandedHand = !isExpandedHand }) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .width(160.dp),
                    value = selectedHand,
                    label = { Text(stringResource(id = R.string.hand)) },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedHand) })
                ExposedDropdownMenu(
                    expanded = isExpandedHand,
                    onDismissRequest = { isExpandedHand = false }) {
                    hands.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                selectedHand = hands[index]
                                isExpandedHand = false
                            })


                    }
                }

            }
        }}

        item{Spacer(modifier = Modifier.height(25.dp))}
        item{Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = distance, onValueChange = { distance = it }, label = {
                Text(
                    stringResource(id = R.string.distance)
                )
            }, modifier = Modifier
                .width(160.dp)
                .padding(9.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(30.dp))
            ExposedDropdownMenuBox(
                expanded = isExpandedWeather,
                onExpandedChange = { isExpandedWeather = !isExpandedWeather }) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .width(160.dp),
                    value = selectedWeather,
                    label = { Text(stringResource(id = R.string.weather)) },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedWeather) })
                ExposedDropdownMenu(
                    expanded = isExpandedWeather,
                    onDismissRequest = { isExpandedWeather = false }) {
                    weather.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                selectedWeather = weather[index]
                                isExpandedWeather = false
                            })


                    }
                }

            }
        }}

        item{ Spacer(modifier = Modifier.height(25.dp))}
        item{ExposedDropdownMenuBox(
            expanded = isExpandedThrow,
            onExpandedChange = { isExpandedThrow = !isExpandedThrow }) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(160.dp)
                    .padding(9.dp),
                value = selectedThrow,
                label = { Text(stringResource(id = R.string.throw_type)) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedThrow) })
            ExposedDropdownMenu(
                expanded = isExpandedThrow,
                onDismissRequest = { isExpandedThrow = false }) {
                throwType.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text = text) },
                        onClick = {
                            selectedThrow = throwType[index]
                            isExpandedThrow = false
                        })


                }
            }

        }}


        item{Spacer(modifier = Modifier.height(30.dp))}
        item{Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(end = 20.dp),
            contentAlignment = Alignment.CenterEnd){


            IconButton(

                onClick = {
                    if (account.value != null && pocetHodov !=null && distance !=null) {

                        activityViewModel.insert(
                            Activity(
                                date = date.value,
                                count = pocetHodov.toInt(),
                                hand = selectedHand,
                                distance = distance.toInt(),
                                weather = selectedWeather,
                                throwType = selectedThrow,
                                accountId = account.value!!.uuidAcc
                            )
                        )
                        val intent = Intent(context, MenuAktivity::class.java)
                        context.startActivity(intent)
                    } else {
                    scope.launch {
                        val snackbarText = context.getString(R.string.snackbar_empty)
                        snackbarHostState.showSnackbar(snackbarText)
                    }
                }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ),

                ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "submit",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

        }


    }}}

}


