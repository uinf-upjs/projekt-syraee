package app.discmaster

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class PravidlaAktivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PravidlaScreen()
        }
    }
}

@Composable
fun PravidlaScreen() {
    WebView(url = "https://docs.google.com/viewer?url=https://szf.sk/wp-content/uploads/2013/12/Pravidla-ultimate.pdf")
}

