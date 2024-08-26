package app.discmaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.discmaster.database.AccountViewModel
import app.discmaster.database.AchievmentViewModel
import app.discmaster.database.entities.Account
import app.discmaster.database.entities.Achievment
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Registration(accountViewModel, achievmentViewModel)
        }
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }

    private val achievmentViewModel: AchievmentViewModel by viewModels {
        AchievmentViewModel.AchievmentViewModelFactory((application as DiscMasterAplication).achievmentRepository)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(accountViewModel: AccountViewModel, achievmentViewModel: AchievmentViewModel) {

    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val clubs = listOf("KEFEAR", "Sky Up", "Paskudy", "Outsiterz", "CENADA", "Tri Veže", "Kus Plastu", "North Side Turzovka", "Banník lásky")
    val hands = listOf(stringResource(id = R.string.right_hand), stringResource(id = R.string.left_hand))
    var selectedClub by remember { mutableStateOf(clubs[0]) }
    var selectedHand by remember { mutableStateOf(hands[0]) }

    val valid = remember { mutableStateOf(false) }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val passwordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isExpandedClubs by remember {
        mutableStateOf(false)
    }

    var isExpandedHand by remember {
        mutableStateOf(false)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        })

    {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_disc),
            contentDescription = "disk",
            modifier = Modifier
                .size(110.dp)
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(stringResource(id = R.string.first_name)) },
            leadingIcon = {Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "First name"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(stringResource(id = R.string.last_name)) },
            leadingIcon = {Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Last name"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = loginName,
            onValueChange = { loginName = it },
            label = { Text(stringResource(id = R.string.username_field)) },
            leadingIcon = {Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "login"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = password,
            onValueChange = { password= it },
            label = { Text(stringResource(id = R.string.password_field))},
            leadingIcon = { Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "password"
            )},
            visualTransformation =  if (passwordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                if (passwordVisible.value) {
                    IconButton(onClick = { passwordVisible.value = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "hide")
                    }
                } else {
                    IconButton(
                        onClick = { passwordVisible.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "show")
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            leadingIcon = {Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "email"
            )},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )

        //https://www.youtube.com/watch?v=5h737wNN-qM
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = isExpandedClubs, onExpandedChange = {isExpandedClubs =! isExpandedClubs}) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(150.dp),
                value = selectedClub,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedClubs)})
            ExposedDropdownMenu(expanded = isExpandedClubs, onDismissRequest = { isExpandedClubs = false }) {
                clubs.forEachIndexed{ index, text ->
                DropdownMenuItem(
                    text = { Text(text = text)},
                    onClick = { selectedClub=clubs[index]
                        isExpandedClubs = false })


                }
            }
            
        }
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = isExpandedHand, onExpandedChange = {isExpandedHand =! isExpandedHand}) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(150.dp),
                value = selectedHand,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedHand)})
            ExposedDropdownMenu(expanded = isExpandedHand, onDismissRequest = { isExpandedHand = false }) {
                hands.forEachIndexed{ index, text ->
                    DropdownMenuItem(
                        text = { Text(text = text)},
                        onClick = { selectedHand=hands[index]
                            isExpandedHand = false })


                }
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if(firstName==""|| lastName=="" || loginName=="" || password=="" || email==""){
                    val snackbarText = context.getString(R.string.snackbar_empty)
                    scope.launch {
                        snackbarHostState.showSnackbar(snackbarText)
                    }
                }
                scope.launch { valid.value = accountViewModel.checkLogin(loginName)
                    if(valid.value){
                        val snackbarText = context.getString(R.string.snackbar_login)
                        scope.launch {
                            snackbarHostState.showSnackbar(snackbarText)
                        }
                } else {
                    achievmentViewModel.clearDatabase()
                        scope.launch {
                            achievmentViewModel.insert(Achievment("Nadšenec do disku", 1000, "-", 0, "-", "-","android.resource://${context.packageName}/drawable/disc_enthusiast" ))
                            achievmentViewModel.insert(Achievment("Závislák do disku",10000,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/disc_addict"))
                            achievmentViewModel.insert(Achievment("Sto-tisícový hádzač",100000,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/hundred_thousand_thrower"))
                            achievmentViewModel.insert(Achievment("Miliónový hádzač",1000000,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/milion_thrower"))
                            achievmentViewModel.insert(Achievment("Obojručne zručný",200,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/balanced_1"))
                            achievmentViewModel.insert(Achievment("Obojručne zručnejší",500,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/balanced_2"))
                            achievmentViewModel.insert(Achievment("Obojručne najzručnejší",1000,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/balanced_3"))
                            achievmentViewModel.insert(Achievment("Vianočný špeciál",1224,"-", 0, "-", "-","android.resource://${context.packageName}/drawable/christmas_special"))
                            achievmentViewModel.insert(Achievment("Mokrý rúcač",500,"-", 0, "daždivo", "-","android.resource://${context.packageName}/drawable/in_rain"))
                            achievmentViewModel.insert(Achievment("Eskimák",500,"-", 0, "sneženie", "-","android.resource://${context.packageName}/drawable/in_snow"))
                            achievmentViewModel.insert(Achievment("Šampión vánku",500,"-", 0, "veterno", "-","android.resource://${context.packageName}/drawable/in_wind"))
                            achievmentViewModel.insert(Achievment("Dlhý backhand",250,"-", 40, "-", "backhand","android.resource://${context.packageName}/drawable/long_backhand"))
                            achievmentViewModel.insert(Achievment("Dlhý forehand",250,"-", 40, "-", "forehand","android.resource://${context.packageName}/drawable/long_forehand"))
                            achievmentViewModel.insert(Achievment("Dlhý overhead",150,"-", 40, "-", "overhead","android.resource://${context.packageName}/drawable/long_overhead"))
                            achievmentViewModel.insert(Achievment("Krátky backhand",1000,"-", 20, "-", "backhand","android.resource://${context.packageName}/drawable/short_backhand"))
                            achievmentViewModel.insert(Achievment("Krátky forehand",1000,"-", 20, "-", "forehand","android.resource://${context.packageName}/drawable/short_forehand"))
                            achievmentViewModel.insert(Achievment("Krátky overhead",600,"-", 20, "-", "overhead","android.resource://${context.packageName}/drawable/short_overhead"))

                        }
                    val hashedPassword = hashPassword(password.trim())
                    val account = Account(
                        firstName.trim(),
                        lastName.trim(),
                        loginName.trim(),
                        hashedPassword,
                        email.trim(),
                        selectedClub,
                        selectedHand
                    )
                    accountViewModel.insert(account)
                    val sharedPref =
                        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putLong("last_reset", System.currentTimeMillis())
                        putInt("global_count", 0)
                        apply()
                    }
                    val intent = Intent(context, LoginAktivity::class.java)
                    context.startActivity(intent)
                }}

            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(id = R.string.register_button))
        }
    }}









}

fun hashPassword(password: String): String{
    val salt = BCrypt.gensalt()
    return BCrypt.hashpw(password, salt)
}




