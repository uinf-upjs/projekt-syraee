package app.discmaster

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import app.discmaster.database.AccountViewModel
import app.discmaster.database.entities.Account
import kotlinx.coroutines.launch

class LoginAktivity : ComponentActivity(){


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




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(accountViewModel: AccountViewModel) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        })

    {

        Column (
            modifier = Modifier.fillMaxSize() .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(painter = painterResource(id = R.drawable.icon), contentDescription = "Login image", modifier = Modifier.padding(bottom = 16.dp))
            Text(text = "vitaj", fontSize=28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(value = username.value,
                onValueChange = { username.value = it },
                label = { Text("username") })

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password.value,
                onValueChange = { password.value = it },
                label = { Text("password")}, visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                accountViewModel.verifyPassword(username.value, password.value) { passwordCheck ->
                    if (passwordCheck){
                        accountViewModel.getIdByLogin(username.value)
                        val intent = Intent(context, MenuAktivity::class.java)
                        context.startActivity(intent)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Nespravne meno alebo heslo")
                        }


                    }


                }}) {
                Text(text = "Prihlasit")
            }

            Button(onClick = {
                val intent = Intent(context, RegistrationActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = "Registrovat sa")
            }

            Spacer(modifier = Modifier.height(100.dp))


            Text(text="Zabudnute heslo?", modifier = Modifier.clickable {

            })


        }

    }


}

