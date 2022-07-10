package com.example.tramstop

import Actual
import Json4Kotlin_Base
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {


    val model: MainActivityViewModel by viewModels()

    var timer: Repo.Tmr? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Content()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        timer = model.getTimer()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }


    @Composable
    fun Content() {
        // A surface container using the 'background' color from the theme
        Surface {
            Timetables()
        }

    }
}

@Composable
fun Timetables(model: MainActivityViewModel = viewModel()) {
    val baseRz by model.rzebika.observeAsState()
    val baseGr by model.grunwald.observeAsState()
    val state by model.state.observeAsState(true)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Selector(state = state)
        if (state) {
            TimeLazyColumn(base = baseRz, state = state)
        } else {
            TimeLazyColumn(base = baseGr, state = state)
        }
        Progress()
    }
}

fun destFilter(state: Boolean, actual: Actual): Boolean {

    Log.d(Repo.TAG, "state: $state")

    if (state) {
        return !actual.direction.contains("Mały")
    } else {
        return !actual.direction.contains(regex = Regex("Borek|Czerwone"))
    }
}

@Composable
fun StopName(state: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(16.dp), horizontalArrangement = Arrangement.Center
    ) {
        Text(fontSize = 24.sp, text = if (state) "Rzebika" else "Grunwaldzkie")
    }
}

@Composable
fun Selector(state: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StopName(state = state)
        Switch(modifier = Modifier.fillMaxWidth(), checked = state, onCheckedChange = {
            Repo.setState(it)
        })
    }

}

@Composable
fun TimeLazyColumn(state: Boolean, base: Json4Kotlin_Base?) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        base?.let {
            items(it.actual) { item ->
                if (destFilter(state, item)) TramItem(actual = item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TramItem(actual: Actual) {
    val fontSize = 20.sp
    var time = actual.mixedTime.split("%")[0]
    if (!time.contains(":")) time += "min"
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Image(
                painter = painterResource(id = R.drawable.tram_24),
                contentDescription = "image",
                alignment = Alignment.Center,
                modifier = Modifier.size(26.dp)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                fontSize = fontSize,
                text = actual.patternText.toString()
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                fontSize = fontSize,
                text = time,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                fontSize = fontSize,
                text = actual.direction,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun Progress(model: MainActivityViewModel = viewModel()) {
    val progress by model.progress.observeAsState()
    LinearProgressIndicator(
        progress!!, modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth()
    )
}
