package app.discmaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat

import app.discmaster.database.AccountViewModel

import kotlinx.coroutines.launch


class LoginAktivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(accountViewModel)
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }
}




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(accountViewModel: AccountViewModel) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val slovak = remember { mutableStateOf(true) }

    val passwordVisible = remember { mutableStateOf(false) }
    val zabudnuteHeslo = remember { mutableStateOf(false) }
    val isPasswordUpdatedSuccessfully = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val locale = remember { mutableStateOf("") }
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("sk"))
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, containerColor = Color(0xFFF3E5F5))

    {

        Column (
            modifier = Modifier
                .fillMaxSize()
                ,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(
            ) {
                Box(
                    modifier = Modifier.align(Alignment.Top)
                ){
                IconButton(
                    onClick = {
                        slovak.value = !slovak.value
                        locale.value = if (slovak.value) "sk" else "en"
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale.value))
                    },
                    modifier = Modifier.padding(end = 30.dp)
                ) {
                    if (slovak.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.slovak),
                            contentDescription = "Slovak",
                            tint = Color.Unspecified
                        )

                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.english),
                            contentDescription = "English",
                            tint = Color.Unspecified
                        )

                    }
                }}
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Login image",
                    modifier = Modifier
                        .align(Alignment.Top)
                        .size(300.dp)
                        .padding(end = 40.dp)
                )
            }


            Text(
                    text = stringResource(id = R.string.greeting),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )


            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = username.value,
                onValueChange = { username.value = it },
                label = { Text(stringResource(id = R.string.username_field)) },
                leadingIcon = { Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "username"
                )})

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = password.value,
                onValueChange = { password.value = it },
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
            Spacer(modifier = Modifier.height(25.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)){


        Button(onClick = {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
        }, modifier = Modifier.height(40.dp)) {
            Text(text = stringResource(id = R.string.register_button))
        }
                Spacer(modifier = Modifier.width(50.dp))
                Button(onClick = {
                    if(username.value == "" || password.value=="") {
                        scope.launch {
                            val snackbarText = context.getString(R.string.snackbar_empty)
                            snackbarHostState.showSnackbar(snackbarText)
                        }
                    } else {
                        accountViewModel.verifyPassword(username.value, password.value) { passwordCheck ->
                            if (passwordCheck){

                                val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putString("logged_in_user", username.value)
                                    putString("language", locale.value)
                                    apply()
                                }
                                val intent = Intent(context, MenuAktivity::class.java)
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            } else {
                                val snackbarText = context.getString(R.string.snackbar_wrong)
                                scope.launch {
                                    snackbarHostState.showSnackbar(snackbarText)
                                }


                            }
                        }}}, modifier = Modifier.height(40.dp))
                {
                    Text(text = stringResource(id = R.string.signIn_button))
                }}

            Spacer(modifier = Modifier.height(25.dp))


            Button(onClick = {
                zabudnuteHeslo.value =! zabudnuteHeslo.value

            }, modifier = Modifier.height(35.dp), colors = ButtonDefaults.buttonColors(containerColor = Gray)) {
                Text(text = stringResource(id = R.string.forgottenPassword_button))

            }


            if (zabudnuteHeslo.value && !isPasswordUpdatedSuccessfully.value) {
                ChangePassword(
                    accountViewModel = accountViewModel,
                    onPasswordUpdate = { isPasswordUpdatedSuccessfully.value = it }
                )
            }



        }

    }


}


@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChangePassword(accountViewModel: AccountViewModel,onPasswordUpdate: (Boolean) -> Unit) {
    val username = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val checkEmail = remember { mutableStateOf("") }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val isUserValid = remember { mutableStateOf(false) }
    var clearFields = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, containerColor = Color.Transparent,

    content = {
        Divider(modifier = Modifier.padding(bottom = 4.dp, start=20.dp, end=20.dp, top = 15.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isUserValid.value) {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    label = { Text(stringResource(id = R.string.username_field)) }
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = checkEmail.value,
                    onValueChange = { checkEmail.value = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    label = { Text("email") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    scope.launch {
                        isUserValid.value = accountViewModel.chechForChangePassword(
                            username.value.trim(),
                            checkEmail.value.trim()
                        )
                        if (isUserValid.value) {
                            val snackbarText = context.getString(R.string.snackbar_verified)
                            snackbarHostState.showSnackbar(snackbarText)
                        } else {
                            val snackbarText = context.getString(R.string.snackbar_wrong)
                            snackbarHostState.showSnackbar(snackbarText)
                        }
                    }
                }) {
                    Text(stringResource(id = R.string.verify_button))
                }
            }

            if (isUserValid.value) {
                Spacer(modifier = Modifier.height(16.dp))


                TextField(
                    value = newPassword.value,
                    onValueChange = { newPassword.value = it },
                    label = { Text(stringResource(id = R.string.newPassword_field)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                Button(onClick = {
                    scope.launch {
                        val userUuid = accountViewModel.getIdByLogin(username.value.trim())
                        if (userUuid != null) {
                            accountViewModel.updatePassword(
                                userUuid,
                                hashPassword(newPassword.value.trim())
                            )
                            val snackbarText = context.getString(R.string.snackbar_successChange)
                            snackbarHostState.showSnackbar(snackbarText)
                            onPasswordUpdate(true)
                        } else {
                            val snackbarText = context.getString(R.string.not_found)
                            snackbarHostState.showSnackbar(snackbarText)
                        }

                    }

                }) {
                    Text(stringResource(id = R.string.updatePassword_button))
                }
            }
        }

    }
        )
}



