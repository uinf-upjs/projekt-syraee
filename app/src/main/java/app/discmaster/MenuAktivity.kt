package app.discmaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.discmaster.database.AccountViewModel
import app.discmaster.database.AchievmentViewModel
import app.discmaster.database.ActivityViewModel
import app.discmaster.database.entities.Event
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MenuAktivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(accountViewModel = accountViewModel, activityViewModel=activityViewModel, achievmentViewModel=achievmentViewModel)
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }

    private val activityViewModel: ActivityViewModel by viewModels {
        ActivityViewModel.ActivityViewModelFactory((application as DiscMasterAplication).activityRepository)
    }
    private val achievmentViewModel: AchievmentViewModel by viewModels {
        AchievmentViewModel.AchievmentViewModelFactory((application as DiscMasterAplication).achievmentRepository)
    }
}


//https://developer.android.com/develop/ui/compose/components/drawer
//
@Composable
@OptIn(ExperimentalMaterial3Api::class)

fun HomeScreen(accountViewModel: AccountViewModel, activityViewModel: ActivityViewModel, achievmentViewModel: AchievmentViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val searchQuery = remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedScreen by remember { mutableStateOf("Home") }
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString("logged_in_user", "Account")
    val language = sharedPref.getString("language", "Default")
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Book, contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.rules_tab)) },
                    selected = false,
                    onClick = { val intent = Intent(context, PravidlaAktivity::class.java)
                        context.startActivity(intent)}
                )
                NavigationDrawerItem(
                    icon = { Icon(imageVector = Icons.Default.SportsHandball, contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.throws_tab)) },
                    selected = false,
                    onClick = { val intent = Intent(context, HodyAktivity::class.java)
                        context.startActivity(intent)}
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {

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

                            Row(
                                modifier = Modifier.fillMaxWidth() .padding(start=45.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        selectedScreen = "HomeScreen"
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Icon(
                                        Icons.Filled.Home,
                                        contentDescription = "Home",
                                        modifier = Modifier.size(37.dp),
                                        tint = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        val intent = Intent(context, AccountAktivity::class.java)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(37.dp),
                                        tint = Color.Black
                                    )
                                }
                            }

                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.height(70.dp),
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
                                        contentDescription = stringResource(id = R.string.activity_heading),
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

                            "Calendar" -> CalendarScreen(accountViewModel, activityViewModel)
                            "AddRecord" -> AddRecordScreen(
                                accountViewModel = accountViewModel,
                                activityViewModel = activityViewModel
                            )
                            "Achievements" -> AchievmentScreen(achievmentViewModel=achievmentViewModel, accountViewModel=accountViewModel)
                            "HomeScreen" -> HomeScreenAktivityFun(accountViewModel = accountViewModel, achievmentViewModel=achievmentViewModel)
                            else -> HomeScreenAktivityFun(accountViewModel = accountViewModel,achievmentViewModel=achievmentViewModel)
                        }
                    }

                }

            )
        }
    )
}

