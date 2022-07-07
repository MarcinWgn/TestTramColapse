package com.example.tramstop

import Actual
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tramstop.ui.theme.TramStopTheme

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
        Log.d(Repo.TAG, "Pause")
    }


    @Composable
    fun Content() {
        // A surface container using the 'background' color from the theme
        Surface() {
            Column {
                TimetableList()
                Progress()
                Timetable2()

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableList(model: MainActivityViewModel = viewModel()) {
    val base by model.rzebika.observeAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        if (base != null) {
            for (item in base?.actual!!) {
                if (!item.direction.contains("Mały"))
                    item {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp)) {
                                val fontSize = 20.sp
                                Image(
                                    painter = painterResource(id = R.drawable.tram_24),
                                    contentDescription = "image",
                                    alignment = Alignment.Center,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = item.patternText.toString(),
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = fontSize
                                )
                                val timeTable = item.mixedTime.split("%")
                                val time = timeTable[0]
                                val min = if (timeTable.size > 1) "min" else ""
                                Text(
                                    text = time,
                                    modifier = Modifier.padding(start = 16.dp),
                                    fontSize = fontSize,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = min,
                                    fontSize = fontSize,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = item.direction,
                                    modifier = Modifier.padding(start = 20.dp),
                                    fontSize = fontSize,
                                    fontStyle = FontStyle.Italic
                                )

                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun Timetable2(model: MainActivityViewModel = viewModel()) {
    val base by model.grunwald.observeAsState()

    LazyColumn(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        base?.let{
            items(it.actual) { item ->
                TramItem(actual = item)
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
            Text(modifier = Modifier.padding(start = 16.dp), fontSize = fontSize
                , text = time, color = MaterialTheme.colorScheme.tertiary)
            Text(modifier = Modifier.padding(start = 8.dp),fontSize = fontSize
                ,text = actual.direction, fontStyle = FontStyle.Italic)

        }
    }
}

@Composable
fun Progress(model: MainActivityViewModel = viewModel()) {
    val progress by model.progress.observeAsState()
    LinearProgressIndicator(
        progress!!, modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TramStopTheme {
        TimetableList()
    }
}