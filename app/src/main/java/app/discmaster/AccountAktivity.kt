package app.discmaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column



import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Observer
import app.discmaster.database.AccountViewModel
import app.discmaster.database.entities.Account
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


class AccountAktivity() : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountScreen(accountViewModel = accountViewModel)
        }

    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }

    fun logout() {
        clearSharedPreferences(this)
        val intent = Intent(this, LoginAktivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AccountScreen(accountViewModel: AccountViewModel) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString("logged_in_user", "Account")
    val language = sharedPref.getString("language", "sk")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    val scope = rememberCoroutineScope()
    if (username != null) {
        scope.launch { accountViewModel.getIdByLogin(username) }

    }

    var account = accountViewModel.account.observeAsState(null)
    var editMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    val clubs = listOf("KEFEAR", "Sky Up", "Paskudy")
    val hands = listOf(stringResource(id = R.string.weak_hand), stringResource(id = R.string.dominant_hand))
    var selectedClub by remember { mutableStateOf(clubs[0]) }
    var selectedHand by remember { mutableStateOf(hands[0]) }


    val zabudnuteHeslo = remember { mutableStateOf(false) }
    val isPasswordUpdatedSuccessfully = remember { mutableStateOf(false) }
    var isExpandedClubs by remember {
        mutableStateOf(false)
    }
    val valid = remember { mutableStateOf(false) }
    var isExpandedHand by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    if (account.value != null && !editMode) {
        name = account.value!!.name
        email = account.value!!.email
        login = account.value!!.login
        selectedClub = account.value!!.club
        selectedHand = account.value!!.mainHand
    }
    val postNotificationPermission = rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    if (account.value!= null) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            })

        {

        Column (modifier = Modifier
            .fillMaxSize()) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.profile_disc),
                    contentDescription = "disk",
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.CenterVertically)
                        .padding(start = 25.dp)
                )
                Spacer(modifier = Modifier.width(35.dp))
                if (editMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .padding(13.dp)
                            .align(Alignment.CenterVertically)
                    )
                } else {
                    Text(
                        text = account.value!!.name,
                        fontSize = 35.sp,
                        modifier = Modifier
                            .padding(13.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Divider(modifier = Modifier.padding(bottom = 4.dp, start = 20.dp, end = 20.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { editMode = !editMode }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "upravit")
                }
                Spacer(modifier = Modifier.width(40.dp))
                IconButton(onClick = {
                    val intent = Intent(context, MenuAktivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(35.dp),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                IconButton(onClick = { (context as AccountAktivity).logout() }) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
                }

            }

            Spacer(modifier = Modifier.height(25.dp))
            Column(modifier = Modifier.padding(start = 40.dp)) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "email",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    if (editMode) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    } else {
                        Text(
                            text = account.value!!.email,
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "uzivatel",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    if (editMode) {
                        OutlinedTextField(
                            value = login,
                            onValueChange = { login = it },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 20.dp)
                        )
                    } else {
                        Text(
                            text = account.value!!.login,
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Icon(
                        imageVector = Icons.Filled.Groups2,
                        contentDescription = "klub",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    if (editMode) {
                        ExposedDropdownMenuBox(
                            expanded = isExpandedClubs,
                            onExpandedChange = { isExpandedClubs = !isExpandedClubs }) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .align(Alignment.CenterVertically),
                                value = selectedClub,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedClubs) })
                            ExposedDropdownMenu(
                                expanded = isExpandedClubs,
                                onDismissRequest = { isExpandedClubs = false }) {
                                clubs.forEachIndexed { index, text ->
                                    DropdownMenuItem(
                                        text = { Text(text = text) },
                                        onClick = {
                                            selectedClub = clubs[index]
                                            isExpandedClubs = false
                                        })


                                }
                            }

                        }
                    } else {
                        Text(
                            text = account.value!!.club,
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Icon(
                        imageVector = Icons.Filled.BackHand,
                        contentDescription = "ruka",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    if (editMode) {
                        ExposedDropdownMenuBox(
                            expanded = isExpandedHand,
                            onExpandedChange = { isExpandedHand = !isExpandedHand }) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .align(Alignment.CenterVertically),
                                value = selectedHand,
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
                    } else {
                        Text(
                            text = account.value!!.mainHand,
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "jazyk",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))

                    Text(
                        text = language.toString(),
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )


                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = "heslo",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = stringResource(id = R.string.change_password), fontSize = 15.sp, modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            zabudnuteHeslo.value = !zabudnuteHeslo.value
                        })


                }
                Spacer(modifier = Modifier.height(4.dp))

            }
            if (zabudnuteHeslo.value && !isPasswordUpdatedSuccessfully.value) {
                ChangePassword(
                    accountViewModel = accountViewModel,
                    onPasswordUpdate = { isPasswordUpdatedSuccessfully.value = it })
            }
            Spacer(modifier = Modifier.height(30.dp))

            if (editMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    IconButton(onClick = { editMode = false }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "zrusit",
                            modifier = Modifier.size(50.dp)
                        )

                    }

                            IconButton(onClick = {
                                val updated = Account(
                                    name = name,
                                    surname = account.value!!.surname,
                                    login = login,
                                    password = account.value!!.password,
                                    email = email,
                                    club = selectedClub,
                                    mainHand = selectedHand,
                                    achievmentId = account.value!!.achievmentId
                                )
                                updated.uuidAcc = account.value!!.uuidAcc
                                scope.launch {
                                    valid.value = accountViewModel.checkLogin(login)
                                if (valid.value) {
                                    val snackbarText = context.getString(R.string.snackbar_login)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(snackbarText)
                                    }} else {
                                accountViewModel.update(updated)
                                with(sharedPref.edit()) {
                                    putString("logged_in_user", login)
                                    apply()
                                }
                                editMode = false
                                scope.launch { accountViewModel.getIdByLogin(login) }
                            }}})
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "ulozit",
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }




                }


            }


        }
    }

    else {
        Text(
            text = "No account found",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
    

    


}




fun clearSharedPreferences(context: Context) {
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with (sharedPref.edit()) {
        clear()
        apply()
    }
}




