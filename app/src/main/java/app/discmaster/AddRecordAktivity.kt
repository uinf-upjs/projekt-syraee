package app.discmaster

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Date

class AddRecordAktivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

// date picker https://www.youtube.com/watch?v=cJxo96eTHVU


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(context : Context) {

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date=remember { mutableStateOf("$day/${month + 1}/$year") }
    val datePickerDialog = DatePickerDialog(context,R.style.DatePickerDialogTheme,
    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
     date.value = "$dayOfMonth/${month + 1}/$year"

        }, year, month, day
    )

    val pocetHodov = remember { mutableStateOf("") }
    val hands = listOf("Dominantna", "Slaba")
    var selectedHand by remember { mutableStateOf(hands[0]) }
    var isExpandedHand by remember {
        mutableStateOf(false)
    }

    val distance = remember { mutableStateOf("") }
    val weather = listOf("Slnecno", "Veterno", "Snezenie", "Dazdivo")
    var selectedWeather by remember { mutableStateOf(weather[0]) }
    var isExpandedWeather by remember {
        mutableStateOf(false)
    }


    val throwType = listOf("forehand", "backhand", "overhead")
    var selectedThrow by remember { mutableStateOf(throwType[0]) }
    var isExpandedThrow by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Zapis hodov",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {

                TextField(value = date.value, onValueChange = {}, label={ Text("Datum")},readOnly = true, modifier = Modifier
                    .padding(9.dp)
                    .width(110.dp))


            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "kalendar" , modifier= Modifier.size(50.dp) )
            }

        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = pocetHodov.value , onValueChange = {}, label = {Text("Pocet hodov")}, modifier = Modifier
                .width(160.dp)
                .padding(9.dp) )

            Spacer(modifier = Modifier.width(30.dp))
            ExposedDropdownMenuBox(expanded = isExpandedHand, onExpandedChange = {isExpandedHand =! isExpandedHand}) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .width(160.dp),
                    value = selectedHand,
                    label = { Text("Ruka")},
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedHand)})
                ExposedDropdownMenu(expanded = isExpandedHand, onDismissRequest = { isExpandedHand = false }) {
                    hands.forEachIndexed{ index, text ->
                        DropdownMenuItem(
                            text = { Text(text = text)},
                            onClick = { selectedHand=hands[index]
                                isExpandedHand = false })


                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = distance.value , onValueChange = {}, label = {Text("Vzdialenost")}, modifier = Modifier
                .width(160.dp)
                .padding(9.dp) )

            Spacer(modifier = Modifier.width(30.dp))
            ExposedDropdownMenuBox(expanded = isExpandedWeather, onExpandedChange = {isExpandedWeather =! isExpandedWeather}) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .width(160.dp),
                    value = selectedWeather,
                    label = { Text("Pocasie")},
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedWeather)})
                ExposedDropdownMenu(expanded = isExpandedWeather, onDismissRequest = { isExpandedWeather = false }) {
                    weather.forEachIndexed{ index, text ->
                        DropdownMenuItem(
                            text = { Text(text = text)},
                            onClick = { selectedWeather=weather[index]
                                isExpandedWeather = false })


                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        ExposedDropdownMenuBox(expanded = isExpandedThrow, onExpandedChange = {isExpandedThrow =! isExpandedThrow}) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(160.dp)
                    .padding(9.dp),
                value = selectedThrow,
                label = { Text("Typ hodu")},
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedThrow)})
            ExposedDropdownMenu(expanded = isExpandedThrow, onDismissRequest = { isExpandedThrow = false }) {
                throwType.forEachIndexed{ index, text ->
                    DropdownMenuItem(
                        text = { Text(text = text)},
                        onClick = { selectedThrow=throwType[index]
                            isExpandedThrow = false })


                }
            }

        }

        Spacer(modifier = Modifier.height(55.dp))
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(0.dp))
                .align(Alignment.End)

        ) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(50.dp)
                    // No rounded corners

            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "submit",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

    }




}


@Preview(showBackground = true)
@Composable
fun AddRecordScreenPreview() {
    AddRecordScreenPreviewContent()
}

@Composable
fun AddRecordScreenPreviewContent() {

    val context = LocalContext.current
    AddRecordScreen(context)
}

