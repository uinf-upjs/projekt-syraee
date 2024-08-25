package app.discmaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.discmaster.database.AccountViewModel
import app.discmaster.ui.theme.DiscMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val loggedInUser = sharedPref.getString("logged_in_user", null)

        if (loggedInUser != null) {
            val intent = Intent(this, MenuAktivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContent {
                val intent = Intent(this, LoginAktivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModel.AccountViewModelFactory((application as DiscMasterAplication).accountRepository)
    }
}

