package app.discmaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.discmaster.database.AccountViewModel
import kotlinx.coroutines.launch

class MenuAktivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(accountViewModel = accountViewModel)
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }
}


//https://developer.android.com/develop/ui/compose/components/drawer
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(accountViewModel: AccountViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val searchQuery = remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedScreen by remember { mutableStateOf("Home") }
    val account = accountViewModel.account

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Book, contentDescription = null) },
                    label = { Text(text = "Pravidla") },
                    selected = false,
                    onClick = { val intent = Intent(context, PravidlaAktivity::class.java)
                        context.startActivity(intent)}
                )
                NavigationDrawerItem(
                    icon = { Icon(imageVector = Icons.Default.SportsHandball, contentDescription = null) },
                    label = { Text(text = "Technika hodov") },
                    selected = false,
                    onClick = { val intent = Intent(context, PravidlaAktivity::class.java)
                        context.startActivity(intent)}
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedTextField(
                                    value = searchQuery.value,
                                    onValueChange = { searchQuery.value = it },

                                    label = { Text("Search")  },
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(65.dp),
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch { drawerState.open()}
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu", modifier = Modifier.size(37.dp), tint = Color.Black)

                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                println(account.value?.login)
                                val intent = Intent(context, AccountAktivity::class.java)
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Filled.Person, contentDescription = "Profile",  modifier = Modifier.size(37.dp), tint = Color.Black)
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        actions = {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                IconButton(
                                    onClick = { selectedScreen = "Calendar" },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarMonth,
                                        contentDescription = "Kalendar",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { selectedScreen = "AddRecord" },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Pridanie hodov",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { selectedScreen = "Achievements" },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.social_leaderboard),
                                        contentDescription = "Achievmenty",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    )
                },
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        when (selectedScreen) {
                            "Calendar" -> CalendarScreen(context)
                            "AddRecord" -> AddRecordScreen(context)
                            "Achievements" -> AchievmentScreen()
                            else -> HomeScreenAktivityFun()
                        }
                    }
                }

            )
        }
    )
}
