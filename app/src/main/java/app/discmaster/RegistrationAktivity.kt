package app.discmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.discmaster.database.AccountViewModel
import app.discmaster.database.entities.Account
import org.mindrot.jbcrypt.BCrypt

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Registration(accountViewModel)
        }
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(accountViewModel: AccountViewModel) {

    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val clubs = listOf("KEFEAR", "Sky Up", "Paskudy")
    val hands = listOf("Prava", "Lava")
    var selectedClub by remember { mutableStateOf(clubs[0]) }
    var selectedHand by remember { mutableStateOf(hands[0]) }




    var isExpandedClubs by remember {
        mutableStateOf(false)
    }

    var isExpandedHand by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = loginName,
            onValueChange = { loginName = it },
            label = { Text("Login Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )

        //https://www.youtube.com/watch?v=5h737wNN-qM

        ExposedDropdownMenuBox(expanded = isExpandedClubs, onExpandedChange = {isExpandedClubs =! isExpandedClubs}) {
            TextField(
                modifier = Modifier.menuAnchor(),
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

        ExposedDropdownMenuBox(expanded = isExpandedHand, onExpandedChange = {isExpandedHand =! isExpandedHand}) {
            TextField(
                modifier = Modifier.menuAnchor(),
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

        Button(
            onClick = {
                val hashedPassword = hashPassword(password)
                val account = Account(firstName, lastName,loginName,hashedPassword, email, selectedClub,selectedHand)
                accountViewModel.insert(account)
                val intent = Intent(context, LoginAktivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register")
        }
    }









}

fun hashPassword(password: String): String{
    val salt = BCrypt.gensalt()
    return BCrypt.hashpw(password, salt)
}




