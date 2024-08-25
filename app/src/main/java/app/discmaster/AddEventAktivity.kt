package app.discmaster


import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PhotoLibrary
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.EventViewModel
import app.discmaster.database.entities.Event
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddEventAktivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddEventScreen(accountViewModel = accountViewModel, eventViewModel = eventViewModel)
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory((application as DiscMasterAplication).eventRepository)
    }
}

// zdroj https://www.youtube.com/watch?v=facHWWkqRm0
// https://www.youtube.com/watch?v=uHX5NB6wHao

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(accountViewModel: AccountViewModel, eventViewModel: EventViewModel){

    val year: Int
    val month: Int
    val day: Int
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date= remember { mutableStateOf(Date()) }
    val datePickerDialog = DatePickerDialog(context,R.style.DatePickerDialogTheme,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->

            calendar.set(year, month, dayOfMonth)
            date.value = calendar.time

        }, year, month, day
    )
    val scope = rememberCoroutineScope()
    var name by  remember {  mutableStateOf("") }
    val place = remember { mutableStateOf("") }

    var file = context.createImageFile()
    var uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)




    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri -> capturedImageUri = uri!!})

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                capturedImageUri = uri
            }
        }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        if(it)
        {
            val text = context.getString(R.string.permission_granted)
            Toast.makeText(context, text , Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            val text = context.getString(R.string.permission_denied)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    val takePicture = { ->
        file = context.createImageFile()
        uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        cameraLauncher.launch(uri)
    }
    val category = listOf("MIX", "OPEN", "FUN")
    var selectedCategory by remember { mutableStateOf(category[0]) }
    var isExpandedCategory by remember {
        mutableStateOf(false)
    }

    val text = remember {
        mutableStateOf("")
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString("logged_in_user", "Account")
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))

    LaunchedEffect(username) {

        accountViewModel.getIdByLogin(username!!)
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
            item {
                Text(
                    text = stringResource(id = R.string.event_heading),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    TextField(value = date.value?.let { SimpleDateFormat("dd.MM.yyyy").format(it) }
                        ?: "",
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

                }
            }

            item { Spacer(modifier = Modifier.height(25.dp)) }

            item {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.name)) },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(9.dp)
                )

            }
            item { Spacer(modifier = Modifier.height(25.dp)) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(value = place.value,
                        onValueChange = { place.value = it },
                        label = { Text(stringResource(id = R.string.place)) },
                        modifier = Modifier
                            .width(160.dp)
                            .padding(9.dp)
                    )

                    Spacer(modifier = Modifier.width(30.dp))
                    ExposedDropdownMenuBox(
                        expanded = isExpandedCategory,
                        onExpandedChange = { isExpandedCategory = !isExpandedCategory }) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor()
                                .width(160.dp),
                            value = selectedCategory,
                            label = { Text(stringResource(id = R.string.category)) },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedCategory) })
                        ExposedDropdownMenu(
                            expanded = isExpandedCategory,
                            onDismissRequest = { isExpandedCategory = false }) {
                            category.forEachIndexed { index, text ->
                                DropdownMenuItem(
                                    text = { Text(text = text) },
                                    onClick = {
                                        selectedCategory = category[index]
                                        isExpandedCategory = false
                                    })


                            }
                        }

                    }
                }
            }

            item { Spacer(modifier = Modifier.height(25.dp)) }


            item {
                TextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    label = { Text(stringResource(id = R.string.text)) },
                    modifier = Modifier
                        .width(400.dp)
                        .padding(9.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (capturedImageUri != Uri.EMPTY) {
                        Image(
                            painter = rememberImagePainter(capturedImageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val permissionCheckResult = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    scope.launch {
                                        takePicture()
                                    }
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = "camera",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PhotoLibrary,
                                contentDescription = "galeria",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(

                        onClick = {
                            if (account.value != null && name !=null && place !=null) {

                                var bajty =
                                    context.contentResolver.openInputStream(capturedImageUri)
                                        ?.readBytes()


                                if (bajty!!.isNotEmpty()) {
                                    eventViewModel.insert(
                                        Event(
                                            name,
                                            date.value,
                                            place.value,
                                            selectedCategory,
                                            text.value,
                                            bajty,
                                            account.value!!.uuidAcc
                                        )
                                    )
                                } else {
                                    eventViewModel.insert(
                                        Event(
                                            name,
                                            date.value,
                                            place.value,
                                            selectedCategory,
                                            text.value,
                                            ByteArray(0),
                                            account.value!!.uuidAcc
                                        )
                                    )
                                }
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
            }
        }
    }

}



fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"

    return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}
